package ch.epfl.alpano.dem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HilbertCreator {

    private final DiscreteElevationModel dem;
    public final static int N = 8192;
    public final static int N_SQUARED = N * N;
    
    private HilbertCreator() {
        this.dem = new SuperHgtDiscreteElevationModel();
    }
    
    public static void buildFiles() throws FileNotFoundException, IOException {
        new HilbertCreator().createFiles();
    }

    private void createFiles() throws FileNotFoundException, IOException {
        int baseLat = 45 * 3600;
        int baseLon = 6 * 3600;
        createFile("HIL01.hgt", baseLat, baseLon);
        createFile("HIL02.hgt", baseLat, baseLon + 8192);
    }

    private void createFile(String fileName, int baseLat, int baseLon) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        if(file.exists() && file.length() == N_SQUARED * 2) {
            System.out.println(fileName + " already exists.");
            return;
        }
        try (FileOutputStream stream = new FileOutputStream(file)) {
            System.out.println("Creating the file " + fileName);
            for (int i = 0; i < N_SQUARED; ++i) {
                int rx, ry, s, t = i, x = 0, y = 0;
                for (s = 1; s < N; s *= 2) {
                    rx = 1 & (t / 2);
                    ry = 1 & (t ^ rx);

                    // ROT
                    if (ry == 0) {
                        if (rx == 1) {
                            x = s - 1 - x;
                            y = s - 1 - y;
                        }

                        // Swap x and y
                        int temp = x;
                        x = y;
                        y = temp;
                    }

                    x += s * rx;
                    y += s * ry;
                    t /= 4;
                }
                short elevation = (short) dem.elevationSample(baseLon + x, baseLat + y);
                byte[] b = new byte[2];
                b[0] = (byte) (elevation >> 8);
                b[1] = (byte) (elevation & 0xff);
                stream.write(b);
            }
            System.out.println(fileName + " created.");
        }

    }
}
