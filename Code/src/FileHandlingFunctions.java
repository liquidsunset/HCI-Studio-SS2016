import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by liquidsunset on 30.05.16.
 */
final class FileHandlingFunctions {

    static boolean createSequence(int size, boolean overwrite) {
        File file = new File(LeapFXConstant.FILE_SEQUENCE_NAME);
        if (file.exists() && !overwrite) {
            return false;
        }

        if (file.exists()) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.print("");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        StringBuilder buffer = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            buffer.append(random.nextInt(LeapFXConstant.COUNT_ELEMENTS));
            if (i != size - 1) {
                buffer.append(", ");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(buffer.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static Integer[] getSequence() {
        Integer[] sequenceNumbers;
        try (Scanner scanner = new Scanner(new File(LeapFXConstant.FILE_SEQUENCE_NAME))) {
            String[] sequence = scanner.next().split(", ");
            sequenceNumbers = new Integer[sequence.length];
            for (int i = 0; i < sequence.length; i++) {
                sequenceNumbers[i] = Integer.parseInt(sequence[i]);
            }
        } catch (IOException e) {
            return null;
        }
        return sequenceNumbers;
    }

    public static boolean saveUserLog(double[] angels, Integer[] sequence, Integer[] elementTouched,
                                      double[] angelsTouched, long time, boolean rightHand) {
        return false;
    }


}
