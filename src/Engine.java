import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Engine {

	public static void main(String[] args) throws IOException {
		// Initialize IO.
		System.err.print("");
		System.out.print("");
		// Set up the initial vars.
		String outputTape = "0000000000000000000000000000000000000000";
		String instructionalTape = "";

		// Get the program.
		System.out.println("Enter program or file (-f ...):");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		instructionalTape = br.readLine();
		if (instructionalTape.startsWith("-f")) {
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(instructionalTape.replaceAll("-f ", ""))));
			instructionalTape = fileReader.readLine();
			fileReader.close();
		}

		// Get the machine state.
		System.out.println("Enter initial machine output state or file (-f ...) (end with \"...\" to make infinite):");
		outputTape = br.readLine();
		if (outputTape.startsWith("-f")) {
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(outputTape.replaceAll("-f ", ""))));
			outputTape = fileReader.readLine();
			fileReader.close();
		}
		br.close();

		// Set the head indices.
		int outputRWHeadIndex = 0;
		int instrReadHeadIndex = 0;
		// Determine if the tape dynamically resizes or not.
		boolean infiniteOutputTape = false;
		if (outputTape.endsWith("...")) {
			infiniteOutputTape = true;
			outputTape = outputTape.replace(".", "");
		}
		// Go!
		try {
			// While we are not at program end
			while (instrReadHeadIndex < instructionalTape.length()) {
				// Pad if need be.
				while (infiniteOutputTape && outputRWHeadIndex > outputTape.length()) {
					outputTape += "0";
				}
				// If no pad and no space then the program has finished.
				if (!infiniteOutputTape && outputRWHeadIndex >= outputTape.length()) {
					break;
				}
				// Get the current character.
				char c = instructionalTape.charAt(instrReadHeadIndex);
				// Misc variables used throughout.
				char next = 0x0000;
				char cur = 0x0000;
				int moveNum = 0;
				int n = 0;
				char target = 0x0000;
				// Switch.
				switch (c) {
					case 'M':
						next = instructionalTape.charAt(++instrReadHeadIndex);
						if (next != '0' && next != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
						n = 0;
						moveNum = 0;
						cur = instructionalTape.charAt(++instrReadHeadIndex);
						if (cur != '0' && cur != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
						while (cur != ':') {
							moveNum += (cur == '1') ? Math.pow(2, n) : 0;
							n++;
							cur = instructionalTape.charAt(++instrReadHeadIndex);
						}
						if (next == '0') {
							instrReadHeadIndex -= moveNum;
						} else {
							instrReadHeadIndex += moveNum;
						}
						break;
					case 'm':
						next = instructionalTape.charAt(++instrReadHeadIndex);
						if (next != '0' && next != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
						n = 0;
						moveNum = 0;
						cur = instructionalTape.charAt(++instrReadHeadIndex);
						if (cur != '0' && cur != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
						while (cur != ':') {
							moveNum += (cur == '1') ? Math.pow(2, n) : 0;
							n++;
							cur = instructionalTape.charAt(++instrReadHeadIndex);
						}
						if (next == '0') {
							outputRWHeadIndex -= moveNum;
						} else {
							outputRWHeadIndex += moveNum;
						}
						break;
					case '^':
						char[] chars = outputTape.toCharArray();
						chars[outputRWHeadIndex] = instructionalTape.charAt(++instrReadHeadIndex);
						outputTape = new String(chars);
						instrReadHeadIndex++;
						break;
					case '?':
						target = outputTape.charAt(outputRWHeadIndex);
						if (target == '1') {
							while (instructionalTape.charAt(instrReadHeadIndex) != ':') {
								instrReadHeadIndex++;
							}
							break;
						} else {
							next = instructionalTape.charAt(++instrReadHeadIndex);
							if (next != '0' && next != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
							n = 0;
							moveNum = 0;
							cur = instructionalTape.charAt(++instrReadHeadIndex);
							if (cur != '0' && cur != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
							while (cur != ':') {
								moveNum += (cur == '1') ? Math.pow(2, n) : 0;
								n++;
								cur = instructionalTape.charAt(++instrReadHeadIndex);
							}
							if (next == '0') {
								instrReadHeadIndex -= moveNum;
							} else {
								instrReadHeadIndex += moveNum;
							}
						}
						break;
					case '!':
						target = outputTape.charAt(outputRWHeadIndex);
						if (target == '1') {
							while (instructionalTape.charAt(instrReadHeadIndex) != ':') {
								instrReadHeadIndex++;
							}
							break;
						} else {
							next = instructionalTape.charAt(++instrReadHeadIndex);
							if (next != '0' && next != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
							n = 0;
							moveNum = 0;
							cur = instructionalTape.charAt(++instrReadHeadIndex);
							if (cur != '0' && cur != '1') throw new Exception("INVALID INSTRUCTION SEQUENCE");
							while (cur != ':') {
								moveNum += (cur == '1') ? Math.pow(2, n) : 0;
								n++;
								cur = instructionalTape.charAt(++instrReadHeadIndex);
							}
							if (next == '0') {
								outputRWHeadIndex -= moveNum;
							} else {
								outputRWHeadIndex += moveNum;
							}
						}
						break;
					case ':':
						instrReadHeadIndex++;
						break;
					case '1':
						instrReadHeadIndex++;
						break;
					case '0':
						instrReadHeadIndex++;
						break;
					default:
						throw new Exception("INVALID INSTRUCTION CHARACTER");
				}
				// Debug code
//																				long startTime = System.currentTimeMillis();
//																				while (System.currentTimeMillis() - startTime < 1500) {
//																					Runtime.getRuntime().freeMemory();
//																				}
//																				String instrPtrPrint = "";
//																				int spaceCount = 0;
//																				while (spaceCount < instrReadHeadIndex) {
//																					instrPtrPrint += " ";
//																					spaceCount++;
//																				}
//																				instrPtrPrint += "^";
//																				String outPtrPrint = "";
//																				spaceCount = 0;
//																				while (spaceCount < outputRWHeadIndex) {
//																					outPtrPrint += " ";
//																					spaceCount++;
//																				}
//																				outPtrPrint += "^";
//																				System.err.println(instructionalTape);
//																				System.err.println(instrPtrPrint);
//																				System.err.println(outputTape);
//																				System.err.println(outPtrPrint);
//																				System.err.println();
				instrReadHeadIndex = (instrReadHeadIndex < 0) ? 0 : instrReadHeadIndex;
			}
			System.out.println(outputTape);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}