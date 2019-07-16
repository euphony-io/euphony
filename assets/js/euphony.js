window.Euphony = (function() {
    function euphony() {
        var about = {
            VERSION: '0.1.0',
            AUTHOR: "Ji-woong Choi"
        };
        this.FFTSIZE = 2048,
        this.BUFFERSIZE = this.FFTSIZE,
        this.PI = 3.141592653589793,
        this.PI2 = this.PI*2,
        this.SAMPLERATE = 44100,
        this.SPAN = 172/2,
        this.CONTEXT = new webkitAudioContext(),
        this.scriptProcessor,
        this.ZEROPOINT = 18000,
        
        this.startPointBuffer = new Array(),
        this.outBuffer = new Array();
        this.CODE = new Array();
    };

    euphony.prototype = {
        
        initChannel: function() {
            this.startPointBuffer = this.crossfadeStaticBuffer(this.makeStaticFrequency(this.ZEROPOINT - this.SPAN));
            for (var i = 0; i < 16; i++)
                this.outBuffer[i] = this.crossfadeStaticBuffer(this.makeStaticFrequency(this.ZEROPOINT + i * this.SPAN));               
            outBufferIndex = 0;
        },
        
        setCode: function(code) {
            this.CODE[0] = this.startPointBuffer;
            for (var i = 1; i < code.length+1; i++) {
                var data = code.charCodeAt(i-1).toString(16);
                
                switch (code.charAt(i-1)) {
                    case '0': case '1': case '2':
                    case '3': case '4': case '5':
                    case '6': case '7': case '8':
                    case '9':
                        this.CODE[i] = this.outBuffer[code.charAt(i-1) - '0'];
                    break;
                    case 'a': case 'b': case 'f':
                    case 'c': case 'd': case 'e':
                        this.CODE[i] = this.outBuffer[code.charAt(i-1) - 'a' + 10];
                    break;    
                }
                console.log(code.charAt(i-1));
            }

			this.CODE[code.length+1] = this.outBuffer[this.makeChecksum(code)];
            this.CODE[code.length+2] = this.outBuffer[this.makeParallelParity(code)];

            outBuffer = this.CODE;
        },
        
        dataProcess: function (e) {
            var outputBuf = e.outputBuffer.getChannelData(0);
            var outputBuf2 = e.outputBuffer.getChannelData(1);
            
            for (var i = 0; i < outputBuf.length; i++)
                outputBuf[i] = outputBuf2[i] = outBuffer[outBufferIndex][i];
            
            if(outBuffer.length == ++outBufferIndex) outBufferIndex = 0;
        },
        
        playBuffer: function() {
            this.scriptProcessor = this.CONTEXT.createScriptProcessor(this.FFTSIZE, 0, 2);
            this.scriptProcessor.loop = true;
            this.scriptProcessor.onaudioprocess = this.dataProcess;
            this.scriptProcessor.connect(this.CONTEXT.destination);

        },
        
        stopBuffer: function() {
            this.scriptProcessor.disconnect();
        },
        
        makeFrequency: function(freq) {
            this.outBuffer = this.makeStaticFrequency(freq);
            outBuffer = this.makeStaticFrequency(freq);
        },
        
        makeStaticFrequency: function(freq){
            buffer = new Array();
            for (var i = 0; i < this.BUFFERSIZE; i++)
                buffer[i] = Math.sin(this.PI2 * freq * (i / this.SAMPLERATE));
            return buffer;
        },
        
        applyCrossfade: function() {
            this.outBuffer = this.crossfadeStaticBuffer(this.outBuffer);
        },
        
        crossfadeStaticBuffer: function(buffer) {
            var mini_window,
                fade_section = this.BUFFERSIZE / 8;
                
            for (var i = 0; i < fade_section; i++) {
                mini_window = i / fade_section;
                buffer[i] *= mini_window;
                buffer[this.BUFFERSIZE - 1 - i] *= mini_window;
            }
            return buffer;
        },
        
        mixRawData: function() {
            for (var i = 0, n = arguments.length; i < n; i++) {
                t_buffer = this.makeStaticFrequency(arguments[i]);
                for (var j = 0; j < this.BUFFERSIZE; j++) {
                    this.outBuffer[j] = (outBuffer[j] + t_buffer[j])/2;    
                }
            }
        },
        
        maximizeVolume: function() {
            //SCAN FOR VOLUME UP
            var max = 0;
            for (var i = 0; i < this.BUFFERSIZE; i++) {
                if (max < Math.abs(outBuffer[i]))
                    max = this.outBuffer[i];
            }
            
            if (32767 == max)
                this.outBuffer[i] *= 32767/max;
        },
        
        makeChecksum: function(code){
            var sum = 0;
            for (var i=0; i < code.length; i++) {
                switch (code.charAt(i)) {
                    case '0': case '1': case '2':
                    case '3': case '4': case '5':
                    case '6': case '7': case '8':
                    case '9':
                    sum += code.charAt(i) - '0';
                    break;
                    case 'a': case 'b': case 'c':
                    case 'd': case 'e': case 'f':
                    sum += code.charAt(i) - 'a' + 10;
                    break;
                }
            }
            sum &= 0xF;
            var checksum = (~sum + 1) & 0xF;
            return checksum;
        },
		
		makeParallelParity: function(code) {
			var parity1 = 0,
				parity2 = 0,
				parity3 = 0,
				parity4 = 0,
				parity;
			
			for(var i=0; i < code.length; i++) {
				var snippet;
				switch (code.charAt(i)) {
                    case '0': case '1': case '2':
                    case '3': case '4': case '5':
                    case '6': case '7': case '8':
                    case '9':
                    snippet = code.charAt(i) - '0';
                    break;
                    case 'a': case 'b': case 'c':
                    case 'd': case 'e': case 'f':
                    snippet = code.charAt(i) - 'a' + 10;
                    break;
                }
				parity1 += ((0x8 & snippet) >> 3);
				parity2 += ((0x4 & snippet) >> 2);
				parity3 += ((0x2 & snippet) >> 1);
				parity4 += (0x1 & snippet);
			}
			
			parity = (parity1&0x1)*8 + (parity2&0x1)*4 + (parity3&0x1)*2 + (parity4&0x1);
			
			console.log("P Parity :: " + parity1 + " " + parity2 + " " + parity3 + " " + parity4 + " = " + parity);
			return parity;
		}
    };
    
    return euphony;
}());