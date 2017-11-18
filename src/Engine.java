import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

/*
1000 (eight)
Alphabet A={0,1,:,^,M,m,?,!}
Machine M={instruction read head, output read/write head, instruction tape, output tape} 
0 - 0 bit. Instructional read head moves right if a 0 is read out of context.
1 - 1 bit. Instructional read head moves right if a 1 is read out of context.
: - Instruction terminator.
^ - Output write token. Write all bits after this instruction until the instruction terminator.
M - Instruction tape move token. First bit after "M" determines left (0) or right (1). Read
the remaining bits (rightmost is MSB) until the instruction terminator as numbers. Move that many
bits in the given direction after the terminator.
m - Output tape move token. Same as above.
? - Conditional token. If the output pointer is pointing at a 1 continue execution
after the next instruction terminator, otherwise read the next bits as an instruction move
instruction and execute.
! - Conditional token. If the output pointer is pointing at a 1 continue execution
after the next instruction terminator, otherwise read the next bits as an instruction move
instruction and execute.
*/
public class Engine {

	// m11:^1M111:00m11:^1:m001:^1:M01:^0: - 01100000...
	// ?1111:^0M111:^1 - compliment first bit
	// ?1111:^0M111:^1:m11:M011111: - compliment entire output/input
	public static void main(String[] args) throws IOException {
		System.err.print("");
		System.out.print("");
		String outputTape = "0000000000000000000000000000000000000000";
		String instructionalTape = "";
		
		System.out.println("Enter program or file (-f ...):");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		instructionalTape = br.readLine();
		if (instructionalTape.startsWith("-f")) {
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(instructionalTape.replaceAll("-f ", ""))));
			instructionalTape = fileReader.readLine();
			fileReader.close();
		}

		System.out.println("Enter initial machine output state or file (-f ...) (end with \"...\" to make infinite):");
		outputTape = br.readLine();
		if (outputTape.startsWith("-f")) {
			BufferedReader fileReader = new BufferedReader(new FileReader(new File(outputTape.replaceAll("-f ", ""))));
			outputTape = fileReader.readLine();
			fileReader.close();
		}
		br.close();
		
		int outputRWHeadIndex = 0;
		int instrReadHeadIndex = 0;
		boolean infiniteOutputTape = false;
		if (outputTape.endsWith("...")) {
			infiniteOutputTape = true;
			outputTape = outputTape.replace(".", "");
		}
		try {
			while (instrReadHeadIndex < instructionalTape.length()) {
				while (infiniteOutputTape && outputRWHeadIndex > outputTape.length()) {
					outputTape += "0";
				}
				if (!infiniteOutputTape && outputRWHeadIndex >= outputTape.length()) {
					break;
				}
				char c = instructionalTape.charAt(instrReadHeadIndex);
				char next = 0x0000;
				char cur = 0x0000;
				int moveNum = 0;
				int n = 0;
				char target = 0x0000;
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
																				long startTime = System.currentTimeMillis();
																				while (System.currentTimeMillis() - startTime < 1500) {
																					Runtime.getRuntime().freeMemory();
																				}
																				String instrPtrPrint = "";
																				int spaceCount = 0;
																				while (spaceCount < instrReadHeadIndex) {
																					instrPtrPrint += " ";
																					spaceCount++;
																				}
																				instrPtrPrint += "^";
																				String outPtrPrint = "";
																				spaceCount = 0;
																				while (spaceCount < outputRWHeadIndex) {
																					outPtrPrint += " ";
																					spaceCount++;
																				}
																				outPtrPrint += "^";
																				System.err.println(instructionalTape);
																				System.err.println(instrPtrPrint);
																				System.err.println(outputTape);
																				System.err.println(outPtrPrint);
																				System.err.println();
				instrReadHeadIndex = (instrReadHeadIndex < 0) ? 0 : instrReadHeadIndex;
			}
			System.out.println(outputTape);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}