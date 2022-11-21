import Jama.LUDecomposition;
import Jama.Matrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.math.BigInteger;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Cifrado de Hill en Java.
 */
public class Hill {

    /**
     * Espacio de caracteres permitido para la encriptación y desencriptación.
     */
    public static final char[] CHARACTERS_SPACE = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * Scanner para pedir datos por consola al usuario.
     */
    public static final Scanner SCANNER = new Scanner(System.in);

/*    public static String genRandomText(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS_SPACE[new Random().nextInt(CHARACTERS_SPACE.length)]);
        }
        return stringBuilder.toString();
    }*/

/*    public static boolean getEncodedKeyMatrix2(char[] key, char[] message) {
        try {
            Matrix matrix = new Matrix(keyChecker(key, message.length));
            double determinant = Math.abs(Math.rint(getDeterminantOf(matrix)));
            long inversoModular = (new BigInteger(String.valueOf((int) determinant)).modInverse(new BigInteger(String.valueOf(CHARACTERS_SPACE.length))).longValue());
            if (determinant == 0 || determinant == CHARACTERS_SPACE.length || determinant % CHARACTERS_SPACE.length == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

//    public static void main(String[] args) {
//        int iterations = 0;
//        int messageCounter = 1;
//        int keyCounter = 2;
//        do {
//            for (int i = 0; i < 10; i++) {
//                char[] message = new char[1];
//                char[] key = new char[1];
//                String value = genRandomText(messageCounter);
//                message = value.toCharArray();
//                System.out.println("M = " + value);
//                boolean validate;
//                String keyValue;
//                do {
//                    keyValue = genRandomText(keyCounter * keyCounter);
//                    key = keyValue.toCharArray();
//                    validate = getEncodedKeyMatrix2(key, message);
//                } while (!validate);
//                System.out.println("K = " + keyValue);
//
//                double[][] encodedKey = keyChecker(key, message.length);
//                double[][] encodedMessage = codificate(message, encodedKey.length);
//                double[][] encodedEncryptedMessage = encrypt(encodedMessage, encodedKey);
//                System.out.println("E = " + getTextMatrix(encodedEncryptedMessage));
//
//                double[][] finalCrypt = decrypt(encodedKey, encodedEncryptedMessage);
//                System.out.println("D = " + getTextMatrix(finalCrypt));
//                System.out.println("\n\n");
//                iterations++;
//                messageCounter++;
//                System.out.println(iterations);
//            }
//            keyCounter++;
//        } while (true);
//    }

    public static void main(String[] args) {
        while (true) {
            int option = inputInt("¡Bienvenido! (Cifrado de Hill)" +
                    "\n\t1. Encriptar una cadena de texto." +
                    "\n\t2. Desencriptar una cadena de texto." +
                    "\n\t3. Ver espacio de caracteres permitidos." +
                    "\n\t0. Salir." +
                    "\nSelccione una opción:");

            if (option == 0) System.exit(0);

            char[] message;
            double[][] encodedKey;
            switch (option) {
                case 1 -> {
                    message = inputString("Ingrese una cadena de texto para cifrar: ").toCharArray();
                    showInfo("[DEBUG]: Cadena ingresada: \n" + Arrays.toString(message) + "\n");

                    encodedKey = getEncodedKeyMatrix(message);
                    showInfo("[DEBUG]: Llave ingresada codificada: \n" + showMatrix(encodedKey));

                    double[][] encodedMessage = codificate(message, encodedKey.length);
                    showInfo("[DEBUG]: Cadena codificada:\n" + showMatrix(encodedMessage));

                    double[][] encodedEncryptedMessage = encrypt(encodedMessage, encodedKey);
                    showInfo("[DEBUG]: Cadena encriptada codificada: \n" + showMatrix(encodedEncryptedMessage));

                    showInfo("Resultado de la encriptación: \n" + getTextMatrix(encodedEncryptedMessage));
                }
                case 2 -> {
                    message = inputString("Ingrese una cadena de texto para descifrar: ").toCharArray();
                    showInfo("[DEBUG]: Cadena de texto ingresada: \n" + Arrays.toString(message) + "\n");

                    encodedKey = getEncodedKeyMatrix(message);
                    showInfo("[DEBUG]: Llave ingresada codificada: \n" + showMatrix(encodedKey));

                    double[][] encodedEncryptedMessage = codificate(message, encodedKey.length);
                    double[][] decrypt = decrypt(encodedKey, encodedEncryptedMessage);

                    showInfo("[DEBUG]: Mensaje codificado desencriptado: \n" + showMatrix(decrypt));
                    showInfo("Resultado de la desencriptación: \n" + getTextMatrix(decrypt));
                }
                case 3 -> showInfo(Arrays.toString(CHARACTERS_SPACE));
                default -> showInfo("La opción ingresada no es válida.");
            }
        }
    }

    public static String getTextMatrix(double[][] matrix) {
        int counter = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < matrix[0].length; i++) {
            for (double[] doubles : matrix) {
                stringBuilder.append(CHARACTERS_SPACE[(int) doubles[counter]]);
            }

            if (counter == matrix[0].length) {
                counter = 0;
            } else {
                counter++;
            }
        }
        return stringBuilder.toString();
    }

    public static double[][] encrypt(double[][] encodedMessage, double[][] encodedKey) {
        RealMatrix msg = new Array2DRowRealMatrix(encodedMessage);
        RealMatrix k = new Array2DRowRealMatrix(encodedKey);
        double[][] aux = k.multiply(msg).getData();
        double[][] encryptedMessage = new double[encodedMessage.length][encodedMessage[0].length];

        for (int i = 0; i < encodedMessage.length; i++) {
            for (int j = 0; j < encodedMessage[i].length; j++) {
                encryptedMessage[i][j] = (int) (aux[i][j] % CHARACTERS_SPACE.length);
            }
        }
        return encryptedMessage;
    }
    //primero sacar el inverso modular de la clave
    //teniendo en cuenta que las matrices estan codificadas sacar la determinante
    //se saca la inversa de la matriz de la clave y multiplicar por la determinante de la matriz clave y ese resultado multiplicar por el inverso modular
    //sacarle el modulo

    public static double[][] decrypt(double[][] encodedKey, double[][] encryptedMessage) {
        double keyDeterminant = Math.abs(Math.rint(getDeterminantOf(new Matrix(encodedKey))));
        long modInverse = (new BigInteger(String.valueOf((int) keyDeterminant)).modInverse(new BigInteger(String.valueOf(CHARACTERS_SPACE.length))).longValue());

        double[][] invertedKey = MatrixUtils.inverse(new Array2DRowRealMatrix(encodedKey)).getData();
        double[][] invertedByDeterminant = new double[invertedKey.length][invertedKey[0].length];

        for (int i = 0; i < invertedKey.length; i++) {
            for (int j = 0; j < invertedKey[0].length; j++) {
                invertedByDeterminant[i][j] = invertedKey[i][j] * keyDeterminant;
                invertedByDeterminant[i][j] *= modInverse;
            }
        }

        RealMatrix finalKey = new Array2DRowRealMatrix(invertedByDeterminant);

        //Mensaje a double y a RealMatrix
        double[][] doubleMessage = new double[encryptedMessage.length][encryptedMessage[0].length];
        for (int i = 0; i < doubleMessage.length; i++) {
            for (int j = 0; j < doubleMessage[0].length; j++) {
                doubleMessage[i][j] = encryptedMessage[i][j];
            }
        }
        RealMatrix realMessageMatrix = new Array2DRowRealMatrix(doubleMessage);

        //Multiplicar y sacar el modulo
        double[][] multiplyResult = finalKey.multiply(realMessageMatrix).getData();

        double[][] aux = new double[multiplyResult.length][multiplyResult[0].length];
        for (int i = 0; i < multiplyResult.length; i++) {
            for (int j = 0; j < multiplyResult[0].length; j++) {
                if (multiplyResult[i][j] < 0) {
                    aux[i][j] = CHARACTERS_SPACE.length - Math.abs(multiplyResult[i][j] % CHARACTERS_SPACE.length);
                } else {
                    aux[i][j] = (multiplyResult[i][j]) % CHARACTERS_SPACE.length;
                }
                aux[i][j] = Math.rint(aux[i][j]);

                if (aux[i][j] == CHARACTERS_SPACE.length) {
                    aux[i][j] = 0;
                }
            }
        }
        return aux;
    }

    /**
     * Obtiene el mensaje ingresado por el usuario codificado dentro de un espacio de caracteres
     * definido previamente.
     * <p>
     * Si el mensaje ingresado no completa la longitud de la matriz definida previamente entonces
     * se completarán los espacios nulos con un caracter aleatorio del espacio de caracteres.
     * <p>
     *
     * @param message Mensaje ingresado por el usuario.
     * @param split   Separador para la matriz.
     * @return Mensaje codificado.
     */
    public static double[][] codificate(char[] message, int split) {
        int aux = ((message.length % split == 0) ? (message.length / split) : ((message.length / split) + 1));
        double[][] msgMatrix = new double[split][aux];
        int counter = 0;

        for (int i = 0; i < msgMatrix[0].length; i++) {
            for (int j = 0; j < split; j++) {
                if (counter >= message.length) {
                    msgMatrix[j][i] = 26;
                    counter++;
                } else {
                    msgMatrix[j][i] = getPosition(message[counter]);
                    counter++;
                }
            }
        }
        return msgMatrix;
    }

    /**
     * Obtiene el valor codificado de una letra definido en un espacio de caracteres.
     *
     * @param c Letra a codificar.
     * @return Codificado de la letra.
     */
    public static int getPosition(final char c) {
        for (int i = 0; i < CHARACTERS_SPACE.length; i++) {
            if (CHARACTERS_SPACE[i] == c) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Obtiene la determinante de una matriz. Es obligatorio que la matriz sea cuadrada, es decir que tenga la misma cantidad de filas y columnas.
     *
     * @param matrix Matriz a calcular determinante.
     * @return Determinante hallada.
     */
    public static double getDeterminantOf(Matrix matrix) {
        LUDecomposition decomposition = new LUDecomposition(matrix);
        return decomposition.det();
    }

    /**
     * Pide al usuario que ingrese una llave para la matriz, luego verifica que sea cuadrada
     * y calcula que su determinante no sea 0, ni la cantidad del espacio de caracteres ni modulo del mismo.
     * <p>
     * Si alguna de las validaciones falla, vuelve a pedir de nuevo la llave, de lo contrario la devuelve.
     *
     * @param message Mensaje a cifrar.
     * @return Matriz con la llave codificada.
     */
    public static double[][] getEncodedKeyMatrix(char[] message) {
        char[] key = inputString("Ingrese la llave del cifrado: ").toCharArray();
        try {
            Matrix matrix = new Matrix(keyChecker(key, message.length));
            double determinant = Math.abs(Math.rint(getDeterminantOf(matrix)));
            long modInverse = (new BigInteger(String.valueOf((int) determinant)).modInverse(new BigInteger(String.valueOf(CHARACTERS_SPACE.length))).longValue());
            if (determinant == 0 || determinant == CHARACTERS_SPACE.length || determinant % CHARACTERS_SPACE.length == 0) {
                showInfo("La llave ingresada no sirve para encriptar, seleccione otra.");
                return getEncodedKeyMatrix(message);
            }
            return keyChecker(key, message.length);
        } catch (Exception e) {
            showInfo("La llave ingresada no es válida. Recuerde que se debe poder formar una matriz cuadrada con ella.");
            return getEncodedKeyMatrix(message);
        }
    }

    /**
     * Devuelve la llave codificada con un espacio de caracteres definido.
     *
     * @param key           Llave ingresada por el usuario.
     * @param messageLength Longitud del mensaje a cifrar
     * @return Llave codificada.
     */
    public static double[][] keyChecker(char[] key, int messageLength) {
        double[][] encodedKey = null;

        for (int i = 2; i < messageLength || i < key.length; i++) {
            if (key.length == i * i) {
                encodedKey = new double[i][i];
                int counter = 0;
                for (int j = 0; j < i; j++) {
                    for (int k = 0; k < i; k++) {
                        for (int m = 0; m < CHARACTERS_SPACE.length; m++) {
                            if (key[counter] == CHARACTERS_SPACE[m]) {
                                encodedKey[k][j] = m;
                                counter++;
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }
        return encodedKey;
    }

    /**
     * Pide al usuario ingresar una cadena de texto, luego la convierte a mayúsculas y finalmente valida que
     * todos los caracteres ingresados se encuentren en el espacio de carácteres permitidos definidos previamente.
     * <p>
     * Si el usuario ingresa un caracter no permitido, se le indicará en un mensaje cuál es el caracter que no se puede ingresar
     * y se le pedirá nuevamente que ingrese el mensaje, de lo contrario se retorna el mensaje.
     *
     * @param message Mensaje informativo para mostrar al usuario que debe hacer.
     * @return Cadena de texto ingresada por el usuario y validada dentro de un espacio de carácteres.
     */
    public static String inputString(Object message) {
        showInfo(message);
        String text = Normalizer.normalize(SCANNER.nextLine().toUpperCase().replace(" ", "_"), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean aux = false;
            for (char value : CHARACTERS_SPACE) {
                if (c == value) {
                    aux = true;
                    break;
                }
            }

            if (!aux) {
                showInfo("La cadena de texto ingresada no puede contener el carácter '" + c + "'.");
                return inputString(message);
            }
        }
        return text;
    }

    /**
     * Muestra una matriz por consola.
     *
     * @param matrix Matriz a mostrar.
     * @return Mensaje con la matriz en formato.
     */
    public static String showMatrix(double[][] matrix) {
        StringBuilder stringBuilder = new StringBuilder();
        for (double[] doubles : matrix) {
            stringBuilder.append(Arrays.toString(doubles)).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Muestra un mensaje informativo al usuario por consola.
     *
     * @param message Mensaje a mostrar por consola.
     */
    public static void showInfo(Object message) {
        System.out.println(message);
    }

    public static int inputInt(Object message) {
        showInfo(message);
        try {
            return Integer.parseInt(SCANNER.nextLine());
        } catch (Exception e) {
            showInfo("El valor ingresado es inválido.");
            return inputInt(message);
        }
    }
}
