import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created by kevin on 5/16/16.
 */
public class Highscore {
    public static void writeHighscore (Dimension dim, int mines, String time){
        try{
            boolean written = false;
            Map<BoardValues, ArrayList<String>> scores = readFullHighscore();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("scores.txt"));
            for (Map.Entry<BoardValues, ArrayList<String>> entry : scores.entrySet()){
                Dimension currentDim = entry.getKey().getDim();
                ArrayList<String> currentScores = entry.getValue();
                if(currentDim.equals(dim) && mines == entry.getKey().getMines()){
                    currentScores.add(time);
                    written = true;
                }
                ArrayList<Double> sort = new ArrayList<>();
                for (String score : currentScores){
                    sort.add(Double.parseDouble(score));
                }
                Collections.sort(sort);
                currentScores.clear();
                for (Double score : sort){
                    currentScores.add("" + score);
                }

                bufferedWriter.write(encryptText("--" + (int)currentDim.getWidth() + "x" +  (int)currentDim.getHeight()));
                for (String score : currentScores){
                    bufferedWriter.newLine();
                    bufferedWriter.write(encryptText(score));
                }
                bufferedWriter.newLine();
                bufferedWriter.write(encryptText("**" + entry.getKey().getMines()));
                bufferedWriter.newLine();
            }

            if (!written){
                bufferedWriter.write(encryptText("--" + (int)dim.getWidth() + "x" +  (int)dim.getHeight()));
                bufferedWriter.newLine();
                bufferedWriter.write(encryptText(time));
                bufferedWriter.newLine();
                bufferedWriter.write(encryptText("**" + mines));
            }

            bufferedWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Map<BoardValues, ArrayList<String>> readFullHighscore (){
        try(BufferedReader br = new BufferedReader(new FileReader("scores.txt"))) {
            String encryptedLine = br.readLine();
            Dimension currentDim = null;
            ArrayList<String> currentScores = new ArrayList<>();
            Map<BoardValues, ArrayList<String>> map = new HashMap<>();

            while (encryptedLine != null){
                String line = decryptText(encryptedLine);
                if (line.startsWith("--")){
                    String fixedLine = line.replace("--", "");
                    int rows = Integer.parseInt(fixedLine.substring(0, fixedLine.indexOf("x")));
                    int cols = Integer.parseInt(fixedLine.substring(fixedLine.indexOf("x")+1));
                    currentDim = new Dimension(rows, cols);
                }
                else if (line.startsWith("**")) {
                    if (currentDim != null)
                        map.put(new BoardValues(currentDim, Integer.parseInt(line.replace("**", ""))), currentScores);
                    currentDim = null;
                    currentScores = new ArrayList<>();
                }
                else
                    currentScores.add(line);

                encryptedLine = br.readLine();
            }
            return map;
        }
        catch (Exception e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static ArrayList<String> readHighscore (BoardValues values){
        for (Map.Entry<BoardValues, ArrayList<String>> entry : readFullHighscore().entrySet()){
            if (entry.getKey().equals(values))
                return entry.getValue();
        }
        return new ArrayList<>();
    }

    private static String encryptText(String text){
        return Base64.getEncoder().encodeToString(text.getBytes());
    }
    private static String decryptText(String text){
        return new String(Base64.getDecoder().decode(text));
    }
}
