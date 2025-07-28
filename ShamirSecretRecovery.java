import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;

public class ShamirSecretRecovery {

    static class Point {
        long x;
        BigInteger y;

        Point(long x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws IOException {
        // ✅ Change file name here to test different cases
        String filename = "testcase2.json";

        JSONObject json = new JSONObject(new String(Files.readAllBytes(new File(filename).toPath())));
        int n = json.getJSONObject("keys").getInt("n");
        int k = json.getJSONObject("keys").getInt("k");

        List<Point> allPoints = new ArrayList<>();
        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;
            long x = Long.parseLong(key);
            JSONObject valueObj = json.getJSONObject(key);
            int base = Integer.parseInt(valueObj.getString("base"));
            BigInteger y = new BigInteger(valueObj.getString("value"), base);
            allPoints.add(new Point(x, y));
        }

        Map<BigInteger, Integer> frequencyMap = new HashMap<>();
        combineAndCompute(allPoints, k, 0, new ArrayList<>(), frequencyMap);

        BigInteger mostFrequentSecret = null;
        int maxCount = 0;
        for (Map.Entry<BigInteger, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentSecret = entry.getKey();
            }
        }

        System.out.println("✅ Most probable secret: " + mostFrequentSecret);
    }

    static void combineAndCompute(List<Point> points, int k, int start,
                                  List<Point> current, Map<BigInteger, Integer> freqMap) {
        if (current.size() == k) {
            BigInteger secret = lagrangeInterpolation(current);
            if (secret.compareTo(BigInteger.valueOf(-1)) != 0) {
                freqMap.put(secret, freqMap.getOrDefault(secret, 0) + 1);
            }
            return;
        }

        for (int i = start; i < points.size(); i++) {
            current.add(points.get(i));
            combineAndCompute(points, k, i + 1, current, freqMap);
            current.remove(current.size() - 1);
        }
    }

    static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i == j) continue;
                BigInteger xj = BigInteger.valueOf(points.get(j).x);
                num = num.multiply(xj.negate());
                den = den.multiply(xi.subtract(xj));
            }

            try {
                BigInteger term = yi.multiply(num).divide(den);
                result = result.add(term);
            } catch (ArithmeticException e) {
                return BigInteger.valueOf(-1); // Skip division by zero or bad data
            }
        }

        return result;
    }
}
