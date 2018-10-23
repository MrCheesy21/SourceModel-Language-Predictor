import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class SourceModel {

    private FileReader input = null;
    private String fileName;
    private int[][] counts = new int[26][26];
    private double[][] probabilities = new double[26][26];

    public SourceModel(String name, String fileName) throws IOException {
        this.fileName = name;
        System.out.println("Training " + getName() + " model ... done");
        File file = new File(fileName);
        try {
            input = new FileReader(file);
            int c = 0;
            int c2 = 0;
            while (((c) != -1) && (c2 != -1)) {
                char character = (char) c;
                char character2 = (char) c2;
                if (Character.isAlphabetic(character)) {
                    while (!Character.isAlphabetic(character2) && c2 != -1) {
                        c2 = input.read();
                        character2 = (char) c2;
                    }
                    character = Character.toLowerCase(character);
                    character2 = Character.toLowerCase(character2);
                    if (Character.isAlphabetic(character)
                        && Character.isAlphabetic(character2)) {
                        int index1 = (int) character;
                        int index2 = (int) character2;
                        if ((index1 >= 97 && index1 < 123)
                            && (index2 >= 97 && index2 < 123)) {
                            counts[index1 - 97][index2 - 97]++;
                        }
                    }
                }
                c = c2;
                c2 = input.read();
            }
        } finally {
            if (input != null) {
                input.close();
            }
        }
        for (int row = 0; row < counts.length; row++) {
            int sum = 0;
            for (int col = 0; col < counts[row].length; col++) {
                sum += counts[row][col];
            }
            for (int col = 0; col < counts[row].length; col++) {
                if (counts[row][col] == 0) {
                    probabilities[row][col] = 0.01;
                } else if (sum != 0) {
                    probabilities[row][col] = (1.0) * counts[row][col] / sum;
                }
            }
        }
    }

    public String getName() {
        return fileName;
    }

    public double probability(String test) {
        double prob = 1.0;
        String lower = test.toLowerCase();
        for (int i = 0; i < test.length() - 1; i++) {
            char c1 = lower.charAt(i);
            char c2 = lower.charAt(i + 1);
            if (Character.isAlphabetic(c1)) {
                int check = 1;
                while (!Character.isAlphabetic(c2)
                    && check + i < test.length()) {
                    c2 = lower.charAt(i + check);
                    check++;
                }
                if (Character.isAlphabetic(c1)
                    && Character.isAlphabetic(c2)) {
                    int row = (int) c1 - 97;
                    int col = (int) c2 - 97;
                    prob *= probabilities[row][col];
                }
            }

        }
        return prob;
    }

    public String toString() {
        String arraymodel = ("Model: " + getName() + "\n");
        arraymodel  += "  a    b    c    d    e    f    g    h    i    j"
                            + "    k    l    m    n    o    p    q    r    s"
                            + "    t    u    v    w    x    y    z\n";
        int letter = 97;
        for (int row = 0; row < probabilities.length; row++) {
            arraymodel += (char) letter + " ";
            letter++;
            for (int col = 0; col < probabilities[row].length; col++) {
                String next = String.format("%.2f ", probabilities[row][col]);
                arraymodel += next;
            }
            arraymodel += "\n";
        }
        return arraymodel;
    }

    public static void main(String[] args) throws IOException {
        SourceModel[] models = new SourceModel[args.length - 1];
        double normal = 0.0;
        double max = 0.0;
        String maxlang = "";
        String test = args[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            String newFile = args[i].substring(0, args[i].indexOf("."));
            models[i] = new SourceModel(newFile, args[i]);
        }
        for (int i = 0; i < models.length; i++) {
            double probability = models[i].probability(test);
            if (probability > max) {
                max = probability;
                maxlang = models[i].getName();
            }
            normal += probability;
        }
        for (int i = 0; i < models.length; i++) {
            double num = models[i].probability(test);
            System.out.printf("Probability that test string is %9s: %.2f%n",
                models[i].getName(), num / normal);
        }
        System.out.println("Analyzing: " + test);
        System.out.printf("Test string is most likely %s.", maxlang);
    }
}
