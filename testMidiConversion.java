package mainPackage;

public class testMidiConversion {
	public static String[] notes = {"C","D","E","F","G","A","B"};
	/** Holds the possible sharps.*/
	public static String[] sharps = {"C#","D#","F#","G#","A#"};
	/** Holds the octave numbers.*/
	public static String[] octave = {"-2","-1", "0", "1", "2", "3","4","5","6","7","8"};
	
	public static void main(String[] args) {
		String note = "D#-3";
		String detectedNote = "", detectedOctave = "";
		for(int i = 0; i<sharps.length;i++)
		{
			if(note.contains(sharps[i]))
			{
				note = note.replaceAll(sharps[i], "");
				detectedNote = sharps[i];
				System.out.println("detected sharp: " + detectedNote);
			}
		}
		for(int i = 0; i<notes.length;i++)
		{
			if(note.contains(notes[i]))
			{
				note = note.replaceAll(notes[i], "");
				detectedNote = notes[i];
				System.out.println("detected note: " + detectedNote);
			}
		}
		for(int i = 0; i<octave.length;i++)
		{
			if(note.equals(octave[i]))
			{
				note = note.replaceAll(octave[i], "");
				detectedOctave = octave[i];
				System.out.println("detected octave: " + detectedOctave);
			}
		}
			
			 
	}

}
