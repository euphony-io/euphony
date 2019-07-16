window.Euphony = (function() {
    function euphony() {
        var about = {
            VERSION: '0.1.0',
            AUTHOR: "Ji-woong Choi"
        };

    };
    
    this.FFTSIZE = 2048,
    this.BUFFERSIZE = this.FFTSIZE,
    this.PI = 3.141592653589793,
    this.PI2 = this.PI*2,
    this.SAMPLERATE = 44100,
    this.SPAN = 172/2,
    this.CONTEXT = new AudioContext(),
    this.scriptProcessor,
    this.ZEROPOINT = 18000,
    
    this.startPointBuffer = new Array(),
    this.outBuffer = new Array();
    this.CODE = new Array();
    this.CODE_IDX = 0;

    var T = this;
    
    euphony.prototype = {
        
        initChannel: function() {
            T.startPointBuffer = this.crossfadeStaticBuffer(this.makeStaticFrequency(T.ZEROPOINT - T.SPAN));
            for (var i = 0; i < 16; i++)
                T.outBuffer[i] = this.crossfadeStaticBuffer(this.makeStaticFrequency(T.ZEROPOINT + i * T.SPAN));               
            T.CODE_IDX = 0;
        },
        
        setCode: function(data) {
            T.CODE[0] = T.startPointBuffer;

            var code = "";
            console.log(data);
            for (var i = 0; i < data.length; i++)
                code += data.charCodeAt(i).toString(16);

            console.log(code);
            for (var i = 0; i < code.length; i++) {
                switch (code.charAt(i)) {
                    case '0': case '1': case '2':
                    case '3': case '4': case '5':
                    case '6': case '7': case '8':
                    case '9':
                        T.CODE[i + 1] = T.outBuffer[code.charAt(i) - '0'];
                    break;
                    case 'a': case 'b': case 'f':
                    case 'c': case 'd': case 'e':
                        T.CODE[i + 1] = T.outBuffer[code.charAt(i) - 'a' + 10];
                    break;    
                }
                console.log(code.charAt(i));
            }

	    T.CODE[code.length+1] = T.outBuffer[this.makeChecksum(code)];
            T.CODE[code.length+2] = T.outBuffer[this.makeParallelParity(code)];
        },
        
        dataProcess: function (e) {
            console.log(e);
            var outputBuf = e.outputBuffer.getChannelData(0);
            var outputBuf2 = e.outputBuffer.getChannelData(1);
            
            for (var i = 0; i < outputBuf.length; i++)
                outputBuf[i] = outputBuf2[i] = T.CODE[T.CODE_IDX][i];
            
            if(T.CODE.length == ++T.CODE_IDX) T.CODE_IDX = 0;
            console.log(T.CODE_IDX);
        },
        
        play: function() {
            var source = T.CONTEXT.createBufferSource();
            source.buffer = T.CONTEXT.createBuffer(2, T.SAMPLERATE*2, T.SAMPLERATE);
            T.scriptProcessor = T.CONTEXT.createScriptProcessor(T.FFTSIZE, 0, 2);
            T.scriptProcessor.loop = true;
            T.scriptProcessor.onaudioprocess = this.dataProcess;

            source.connect(T.scriptProcessor);
            T.scriptProcessor.connect(T.CONTEXT.destination);
            source.start();
            /* scriptProcessor is deprecated. so change it.
            let ctx = new OfflineAudioContext(2, 1, T.SAMPLERATE);
            let audioWorklet = ctx.audioWorklet;
            console.log(audioWorklet);
            audioWorklet.addModule('/euphony/assets/js/euphony-processor.js').then(() => {
                let oscillator = new OscillatorNode(ctx);
                let euphonyWorkletNode = new AudioWorkletNode(ctx, 'euphony-processor');

                oscillator.connect(euphonyWorkletNode).connect(ctx.destination);
                oscillator.start();
            });*/
        },
        
        stop: function() {
            T.scriptProcessor.disconnect();
        },
        
        makeStaticFrequency: function(freq){
            buffer = new Array();
            for (var i = 0; i < T.BUFFERSIZE; i++)
                buffer[i] = Math.sin(T.PI2 * freq * (i / T.SAMPLERATE));
            return buffer;
        },
        
        crossfadeStaticBuffer: function(buffer) {
            var mini_window,
                fade_section = T.BUFFERSIZE / 8;

            for (var i = 0; i < fade_section; i++) {
                mini_window = i / fade_section;
                console.log(buffer[i]);
                buffer[i] *= mini_window;
                console.log(mini_window, i, buffer[i]);
                buffer[T.BUFFERSIZE - 1 - i] *= mini_window;
            }
            return buffer;
        },
        
        mixRawData: function() {
            for (var i = 0, n = arguments.length; i < n; i++) {
                t_buffer = this.makeStaticFrequency(arguments[i]);
                for (var j = 0; j < T.BUFFERSIZE; j++) {
                    this.outBuffer[j] = (outBuffer[j] + t_buffer[j])/2;    
                }
            }
        },
        
        maximizeVolume: function() {
            //SCAN FOR VOLUME UP
            var max = 0;
            for (var i = 0; i < T.BUFFERSIZE; i++) {
                if (max < Math.abs(outBuffer[i]))
                    max = T.outBuffer[i];
            }
            
            if (32767 == max)
                T.outBuffer[i] *= 32767/max;
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
