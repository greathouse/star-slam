package starslam


class DataGenerator {
    private static Random random = new Random()

    public static int randomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
