import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
            buffer.append(random.nextInt(LeapFXConstant.COUNT_ELEMENTS) + 1);
            if (i != size - 1) {
                buffer.append(",");
            }
        }

        return saveFile(file, buffer);
    }

    static Integer[] getSequence() {
        Integer[] sequenceNumbers;

        if (!LeapFXConstant.USE_DEFINED_SEQUENCE) {
            try (Scanner scanner = new Scanner(new File(LeapFXConstant.FILE_SEQUENCE_NAME))) {
                String[] sequence = scanner.next().split(",");
                sequenceNumbers = new Integer[sequence.length];
                for (int i = 0; i < sequence.length; i++) {
                    sequenceNumbers[i] = Integer.parseInt(sequence[i]);
                }
            } catch (IOException e) {
                return null;
            }
        } else {
            if (LeapFXConstant.COUNT_ELEMENTS == 3) {
                sequenceNumbers = LeapFXConstant.SEQUENCE_THREE_ELEMENTS;
            } else {
                sequenceNumbers = LeapFXConstant.SEQUENCE_FOUR_ELEMENTS;
            }
        }

        return sequenceNumbers;
    }

    static boolean saveUserLog(double[] angels, Integer[] sequence, Integer[] elementTouched,
                               double[] angelsTouched, long[] timePerElement, long time) {

        File alreadyCreatedLogs = new File(".");
        FilenameFilter filter = (dir, name) -> name.contains(LeapFXConstant.LOG_NAME);

        String logName = LeapFXConstant.LOG_NAME;
        String systemLineSeparator = System.lineSeparator();
        File[] files = alreadyCreatedLogs.listFiles(filter);

        if (files.length > 0) {
            logName += files.length + 1 + "_ElementSize" + LeapFXConstant.COUNT_ELEMENTS + ".txt";
        } else {
            logName += 1 + "_ElementSize" + LeapFXConstant.COUNT_ELEMENTS + ".txt";
        }

        File fileToSave = new File(logName);

        StringBuilder buffer = new StringBuilder();
        buffer.append("Used Angels: ");
        buffer.append(Arrays.toString(angels)).append(systemLineSeparator);
        buffer.append("Element Sequence: ");
        buffer.append(Arrays.toString(sequence)).append(systemLineSeparator);
        buffer.append("Elements touched: ");
        buffer.append(Arrays.toString(elementTouched)).append(systemLineSeparator);
        buffer.append("Angels touched: ");
        buffer.append(Arrays.toString(angelsTouched)).append(systemLineSeparator);
        buffer.append("Duration: ");
        buffer.append(TimeUnit.MILLISECONDS.toMinutes(time)).append(" minutes ")
                .append("and ");
        buffer.append(TimeUnit.MILLISECONDS.toSeconds(time) % 60).append(" seconds")
                .append(systemLineSeparator);
        buffer.append("Pause between elements: ").append(LeapFXConstant.TIME_OUT_IN_MS)
                .append(" ms").append(systemLineSeparator).append(systemLineSeparator);

        int touchedWrong = 0;

        for (int i = 0; i < sequence.length; i++) {
            buffer.append("User should have touched element: ").append(sequence[i]);
            buffer.append(systemLineSeparator);
            buffer.append("User touched element: ").append(elementTouched[i]);
            buffer.append(" at angel: ").append(angelsTouched[i]).append(systemLineSeparator);
            buffer.append("Time between elements: ");
            buffer.append(TimeUnit.MILLISECONDS.toMillis(timePerElement[i]))
                    .append(" ms").append(systemLineSeparator);
            buffer.append("Defined element angels: ").append(Arrays.toString(
                    getElementAngels(sequence[i]))).append(systemLineSeparator).append(
                    systemLineSeparator);

            if (!sequence[i].equals(elementTouched[i])) {
                touchedWrong++;
            }

        }

        BigDecimal angle = new BigDecimal(((double) touchedWrong /
                (double) sequence.length) * 100.0);
        buffer.append(systemLineSeparator);
        buffer.append("Error rate in %: ");
        buffer.append(angle.setScale(LeapFXConstant.DOUBLE_ROUND_DECIMAL,
                BigDecimal.ROUND_HALF_UP).doubleValue());

        return saveFile(fileToSave, buffer);
    }

    private static boolean saveFile(File file, StringBuilder buffer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(buffer.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static double[] getElementAngels(int elem) {

        double[] definedAngels = LeapCalcFunctions.getDefinedAngels();

        if (definedAngels == null) {
            return null;
        }

        double[] angels = new double[2];
        angels[0] = definedAngels[elem - 1];
        angels[1] = definedAngels[elem];

        return angels;
    }
}
