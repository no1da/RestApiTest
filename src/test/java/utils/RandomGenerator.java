package utils;

import java.util.Random;

public class RandomGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String DOMAIN = "example.com";

    /**
     * Генерирует случайное слово заданной длины.
     * @param length Длина генерируемого слова.
     * @return Случайное слово.
     */
    public String generateRandomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            word.append(CHARACTERS.charAt(index));
        }

        return word.toString();
    }

    /**
     * Генерирует случайный email.
     * @return Случайный email.
     */
    public String generateRandomEmail() {
        String localPart = generateRandomWord(8);
        return localPart + "@" + DOMAIN;
    }

}
