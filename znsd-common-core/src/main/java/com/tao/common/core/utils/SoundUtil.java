package com.tao.common.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundUtil {

	public static void play(Object input) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		AudioInputStream in = null;
		SourceDataLine line = null;
		
		try {
			if(input instanceof InputStream){
				in = AudioSystem.getAudioInputStream((InputStream)input);
			}else{
				in = AudioSystem.getAudioInputStream((File)input); 
			}
			
			AudioFormat format = in.getFormat();
			if(format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED){
				format = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED, 
						format.getSampleRate(), 
						16, 
						format.getChannels(),
						format.getChannels() * 2,
						format.getSampleRate(),
						false);
				in = AudioSystem.getAudioInputStream(format, in);
			}
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, in.getFormat(), AudioSystem.NOT_SPECIFIED);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(in.getFormat(), line.getBufferSize());
			line.start();
			
			int numRead = 0;
			byte[] buf = new byte[line.getBufferSize()];
			while((numRead = in.read(buf, 0, buf.length))>=0){
				int offset = 0;
				while(offset<numRead){
					offset += line.write(buf, offset, numRead-offset);
				}
			}
			line.drain();
			line.stop();
		}finally{
			if(null!=line)try {line.close();} catch (Exception e) {}
			if(null!=in)try {in.close();} catch (IOException e) {}
		}
	}
	
//	public static void playMedia(File f){
//		MediaLocator locator = new MediaLocator("file:"
//				+ f.getAbsolutePath());
//		Player player = null;
//		try {
//			player = Manager.createRealizedPlayer(locator);
//			player.prefetch();
//			player.start();
//		} catch (NoPlayerException e) {
//			e.printStackTrace();
//		} catch (CannotRealizeException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			if(null!=player)try {player.close();} catch (Exception e) {}
//		}
//		
//	}
//	
//	public static void main(String[] args) throws FileNotFoundException{
//		InputStream in = SoundUtil.class.getResourceAsStream("/success.wav");
//		SoundUtil.play(in);
//	}
}
