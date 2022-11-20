import Jama.LUDecomposition;
import Jama.Matrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.math.BigInteger;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Cifrado de Hill en Java.
 */
public class Hill {

    /**
     * Espacio de caracteres permitido para la encriptación y desencriptación.
     */
    public static final char[] CHARACTERS_SPACE = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_'};

    /**
     * Scanner para pedir datos por consola al usuario.
     */
    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        String stringMessage = inputString("Ingrese un mensaje a cifrar: ");
        System.out.println(stringMessage);
        char[] message = stringMessage.toCharArray();
        showInfo("[DEBUG]: Mensaje a cifrar: \n" + Arrays.toString(message) + "\n");

        double[][] encodedKey = getEncodedKeyMatrix(message);
        showInfo("[DEBUG]: Llave codificada:\n" + showMatrix(encodedKey));

        double[][] encodedMessage = codificate(message, encodedKey.length);
        showInfo("[DEBUG]: Mensaje codificado:\n" + showMatrix(encodedMessage));

        double[][] encodedEncryptedMessage = encrypt(encodedMessage, encodedKey);
        showInfo("[DEBUG]: Mensaje encriptado codificado: \n" + showMatrix(encodedEncryptedMessage));

        StringBuilder decodedEncryptedMessage = new StringBuilder();

        int counter = 0;
        for (int i = 0; i < encodedEncryptedMessage[0].length; i++) {

            for (double[] doubles : encodedEncryptedMessage) {
                char value = CHARACTERS_SPACE[(int) doubles[counter]];
                decodedEncryptedMessage.append(value);
            }

            if (counter == encodedEncryptedMessage.length) {
                counter = 0;
            } else {
                counter++;
            }
        }
        showInfo("[DEBUG]: Mensaje encriptado decodificado: \n" + decodedEncryptedMessage + "\n");


        System.out.println("------------{DECRYPT}-------------");
        double[][] finalCrypt = desencriptar(encodedKey, encodedEncryptedMessage);
        showInfo("[DEBUG]: Mensaje codificado desencriptado: \n" + showMatrix(finalCrypt));
        StringBuilder mensajeFinal = new StringBuilder();
        System.out.println("Mensaje desencriptado \n");

        counter = 0;
        for (int i = 0; i < finalCrypt[0].length; i++) {

            for (int j = 0; j < finalCrypt.length; j++) {

                System.out.println("num array =" + finalCrypt[j][counter]);
                System.out.println("j =" + j);
                System.out.println("c =" + counter);
                char value = CHARACTERS_SPACE[(int) finalCrypt[j][counter]];
                mensajeFinal.append(value);
            }

            if (counter == finalCrypt[0].length) {
                counter = 0;
            } else {
                counter++;
            }
        }

        System.out.println(mensajeFinal);

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

    public static double[][] desencriptar(double[][] encodedKey, double[][] encryptedMessage) {
        double keyDeterminant = Math.abs(Math.rint(getDeterminantOf(new Matrix(encodedKey))));
        long inversoModular = (new BigInteger(String.valueOf((int) keyDeterminant)).modInverse(new BigInteger(String.valueOf(CHARACTERS_SPACE.length))).longValue());

        //pasandolo a realmatrix
        RealMatrix kRealMatrix = new Array2DRowRealMatrix(encodedKey);
        RealMatrix kInverted = MatrixUtils.inverse(kRealMatrix);

        double[][] kInvedtedInDouble = kInverted.getData();

        //Multiplicar por la determinante de la matriz

        double[][] invertidaPorLaDeter = new double[kInvedtedInDouble.length][kInvedtedInDouble[0].length];

        for (int i = 0; i < kInvedtedInDouble.length; i++) {
            for (int j = 0; j < kInvedtedInDouble[0].length; j++) {
                invertidaPorLaDeter[i][j] = kInvedtedInDouble[i][j] * keyDeterminant;
                invertidaPorLaDeter[i][j] *= inversoModular;
            }
        }

        RealMatrix claveFinal = new Array2DRowRealMatrix(invertidaPorLaDeter);

        //Mensaje a double y a RealMatrix
        double[][] mensajeDouble = new double[encryptedMessage.length][encryptedMessage[0].length];
        for (int i = 0; i < mensajeDouble.length; i++) {
            for (int j = 0; j < mensajeDouble[0].length; j++) {
                mensajeDouble[i][j] = encryptedMessage[i][j];
            }
        }
        RealMatrix mensajeRealMatrix = new Array2DRowRealMatrix(mensajeDouble);

        //Multiplicar y sacar el modulo
        RealMatrix multiplicacion = claveFinal.multiply(mensajeRealMatrix);

        double[][] multiplicacionDouble = multiplicacion.getData();

        double[][] aux = new double[multiplicacionDouble.length][multiplicacionDouble[0].length];
        for (int i = 0; i < multiplicacionDouble.length; i++) {
            for (int j = 0; j < multiplicacionDouble[0].length; j++) {
                if (multiplicacionDouble[i][j] < 0) {
                    aux[i][j] = CHARACTERS_SPACE.length - Math.abs(multiplicacionDouble[i][j] % CHARACTERS_SPACE.length);
                    aux[i][j] = Math.rint(aux[i][j]);
                } else {
                    aux[i][j] = (multiplicacionDouble[i][j]) % CHARACTERS_SPACE.length;
                    aux[i][j] = Math.rint(aux[i][j]);
                }

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
        double[] extras = new double[(aux * split) - message.length];
        int exCounter = 0;

        for (int i = 0; i < msgMatrix[0].length; i++) {
            for (int j = 0; j < split; j++) {
                int extra;
                if (counter >= message.length) {
                    extra = (int) (Math.random() * CHARACTERS_SPACE.length);
                    msgMatrix[j][i] = extra;
                    extras[exCounter] = extra;
                    counter++;
                    exCounter++;
                } else {
                    msgMatrix[j][i] = getPosition(message[counter]);
                    counter++;
                }
            }
        }
        showInfo("[DEBUG]: Caracteres extras: " + Arrays.toString(extras));
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
}
