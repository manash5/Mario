package engine;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

// This class is about creating a simple sound system for playing audio files in a game or application
// It uses OpenAL, a library for working with audio in a 3D space, and stb_vorbis, a library for decoding .ogg audio files.

public class Sound {
    private int bufferId;
    private int sourceId;
    private String filepath;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops){
        this.filepath = filepath;

        // Allocate space to store the return information from stb
        // These buffers will store information about the audio file
        // (like number of channels and sample rate) after decoding it.
        stackPush(); // Saves the current state of the memory stack. This allows temporary memory allocations.
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        // here at first it reads and decodes .ogg file at filepath
        // Outputs the raw audio data as ShortBuffer
        // Fills channelsBuffer with the number of audio channels (eg. mono or stereo)
        // In headphones where there is mono you can only listen at one side but at stereo you can at both
        // Fills sampleRateBuffer with the sample rate (audio quality in Hz).
        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);
        if (rawAudioBuffer == null){
            System.out.println("Could not load sound " + filepath  + "'" );
            stackPop(); // removes the content
            stackPop();
            return;
        }

        // Retrieve the extra information that was stored in the buffers by stb
        // i.e. extracts the no. of channels and sample rate from their respective buffers
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        // Free
        stackPop();
        stackPop();

        // Find the correct openAl format
        // Checks the number of channels (if 1 then Mono if 2 then stereo)
        int format = -1;
        if (channels == 1){
            format = AL_FORMAT_MONO16;
        } else if (channels == 2){
            format = AL_FORMAT_STEREO16;
        }

        // creates a bufferId and stores the raw audio and format
        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        // Generate the source
        // this source is like the speaker that plays the sound stored in a buffer
        sourceId = alGenSources();

        alSourcei(sourceId, AL_BUFFER, bufferId); // links source to the audio buffer
        alSourcei(sourceId, AL_LOOPING, loops? 1: 0); // check if we have to loop or not
        alSourcei(sourceId, AL_POSITION, 0); // sets the position of the source
        alSourcef(sourceId, AL_GAIN, 0.3f); // sets the 30% volume of the maximum volume

        // Free the stb raw audio buffer
        free(rawAudioBuffer);
    }

    // Deletes the sounds
    public void delete(){
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    // Plays the sound
    public void play(){
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED){
            isPlaying = false;
            alSourcei(sourceId, AL_POSITION, 0);
        }

        if (!isPlaying){
            alSourcePlay(sourceId);
            isPlaying = true;
        }

    }

    // Stops the sound
    public void stop(){
        if (isPlaying){
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    // give the file path
    public String getFilepath(){
        return this.filepath;
    }

    // check the state of the song and returns if it is playing or not
    public boolean isPlaying(){
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED){
            isPlaying = false;
        }
        return isPlaying;
    }
}
