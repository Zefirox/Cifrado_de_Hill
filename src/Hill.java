import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

public class Hill {

    public static final char[] CHARACTERS_SPACE = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {

        char[] message = inputString("Ingrese un mensaje a cifrar:").toCharArray();
        int[][] key = keyChecker(inputString("Ingrese la llave:"), message.length);

        System.out.println("Llave:");
        for (int[] c : key) {
            System.out.println(Arrays.toString(c));
        }
        System.out.println("\n");
        int[][] msg = codificate(message, key.length);
        System.out.println("Mensaje");
        for (int[] c : msg) {
            System.out.println(Arrays.toString(c));
        }
        System.out.println("\n");
        int[][] newMessage = encrypt(msg, key);
        System.out.println("Mensaje Encriptado");
        for (int[] c : newMessage) {
            System.out.println(Arrays.toString(c));
        }
        System.out.println("\nNuevo Mensaje");
        StringBuilder mensaje = new StringBuilder();
        for (int i = 0; i < newMessage[i].length; i++) {
            for (int j = 0; j < newMessage.length; j++) {
                mensaje.append(CHARACTERS_SPACE[newMessage[j][i]]);
            }
        }
        System.out.println(mensaje);

        System.out.println("------------{DECRYPT}-------------");
        int[][] finalCrypt = decrypt(key, newMessage);
        StringBuilder mensajeFinal = new StringBuilder();
        System.out.println("Mensaje desencriptado \n");
        for (int i = 0; i < finalCrypt[i].length; i++) {
            for (int j = 0; j < finalCrypt.length; j++) {
                mensajeFinal.append(CHARACTERS_SPACE[finalCrypt[j][i]]);
            }
        }
        System.out.println(mensajeFinal);


    }

    public static int[][] encrypt(int[][] message, int[][] key) {

        double[][] msgDouble = new double[message.length][message[0].length];
        double[][] keyDouble = new double[key.length][key[0].length];

        for (int i = 0; i < msgDouble.length; i++) {
            for (int j = 0; j < msgDouble[i].length; j++) {

                msgDouble[i][j] = message[i][j];

            }
        }
        for (int i = 0; i < keyDouble.length; i++) {
            for (int j = 0; j < keyDouble[i].length; j++) {

                keyDouble[i][j] = key[i][j];

            }
        }

        RealMatrix msg = new Array2DRowRealMatrix(msgDouble);
        RealMatrix k = new Array2DRowRealMatrix(keyDouble);

        double[][] aux = k.multiply(msg).getData();

        int[][] newMsg = new int[message.length][message[0].length];

        for (int i = 0; i < msgDouble.length; i++) {
            for (int j = 0; j < msgDouble[i].length; j++) {

                newMsg[i][j] = (int) (aux[i][j] % CHARACTERS_SPACE.length);

            }
        }

        return newMsg;
    }

    public static int[][] decrypt(int[][] k, int[][] encryptedMsg) {
        double[][] kDouble = new double[k.length][k[0].length];
        double[][] encryptedMessageDouble = new double[encryptedMsg.length][encryptedMsg[0].length];

        for (int i = 0; i < kDouble.length; i++) {
            for (int j = 0; j < kDouble[0].length; j++) {
                kDouble[i][j] = k[i][j];
            }
        }

        for (int i = 0; i < encryptedMessageDouble.length; i++) {
            for (int j = 0; j < encryptedMessageDouble[0].length; j++) {
                encryptedMessageDouble[i][j] = encryptedMsg[i][j];
            }
        }


        RealMatrix kDoubleReal = new Array2DRowRealMatrix(kDouble);
        RealMatrix kInverted = MatrixUtils.inverse(kDoubleReal);

        double[][] kInvertedToPrimitive = kInverted.getData();

        for (int i = 0; i < kInvertedToPrimitive.length; i++) {
            for (int j = 0; j < kInvertedToPrimitive[0].length; j++) {
                if (kInvertedToPrimitive[i][j] < 0) {
                    kInvertedToPrimitive[i][j] = (kInvertedToPrimitive[i][j] % CHARACTERS_SPACE.length);
                }
            }
        }

        double determinanteOriginal = determinanteMatriz(k, k.length);
        int inversoModular = 0;
        for (int i = 0; i < determinanteOriginal; i++) {
            if ((determinanteOriginal * i) % CHARACTERS_SPACE.length == 1) {
                inversoModular = i;
            }
        }
        //multiplicar a todos los elementos de la inversa
        for (int i = 0; i < kInvertedToPrimitive.length; i++) {
            for (int j = 0; j < kInvertedToPrimitive[0].length; j++) {
                double aux1 = ((kInvertedToPrimitive[i][j] * inversoModular * determinanteOriginal) % CHARACTERS_SPACE.length);
                if (aux1 < 0) {
                    aux1 = aux1 * -1;
                }
                kInvertedToPrimitive[i][j] = Math.rint(aux1);
            }
        }

        System.out.println(Arrays.deepToString(kInvertedToPrimitive));

        //Hacer la desencriptacion
        RealMatrix kInvertedToReal = new Array2DRowRealMatrix(kInvertedToPrimitive);

        //Convertir la llave a real
        RealMatrix msgConvertedToReal = new Array2DRowRealMatrix(encryptedMessageDouble);

        //Multiplicar las matrices y aplicar el modulo
        double[][] auxArray = kInvertedToReal.multiply(msgConvertedToReal).getData();
        int[][] desencriptado = new int[encryptedMsg.length][encryptedMsg[0].length];

        for (int i = 0; i < desencriptado.length; i++) {
            for (int j = 0; j < desencriptado[i].length; j++) {
                desencriptado[i][j] = (int) (auxArray[i][j] % CHARACTERS_SPACE.length);
            }
        }
        return desencriptado;
    }

    public static int[][] codificate(char[] msgChar, int split) {

        int aux;

        if (msgChar.length % split == 0) {
            aux = msgChar.length / split;
        } else {
            aux = (msgChar.length / split) + 1;
        }

        int[][] msgMatrix = new int[split][aux];
        int cont = 0;
        int[] extras = new int[(aux * split) - msgChar.length];
        int exCont = 0;

        for (int i = 0; i < msgMatrix[0].length; i++) {
            for (int j = 0; j < split; j++) {
                int extra = 0;
                if (cont >= msgChar.length) {
                    extra = (int) (Math.random() * CHARACTERS_SPACE.length);
                    msgMatrix[j][i] = extra;
                    extras[exCont] = extra;
                    cont++;
                    exCont++;
                } else {
                    msgMatrix[j][i] = getPosition(msgChar[cont]);
                    cont++;
                }
            }
        }
        System.out.println("Caracteres extras: " + Arrays.toString(extras));

        return msgMatrix;
    }

    private static int getPosition(final char c) {
        for (int i = 0; i < CHARACTERS_SPACE.length; i++) {
            if (CHARACTERS_SPACE[i] == c) {
                return i;
            }
        }
        return 0;
    }

    public static int inputInt(Object message, int max) {
        try {
            int number = Integer.parseInt(SCANNER.nextLine());
            if (number > max) {
                showInfo("El valor ingresado no puede ser mayor a " + max + ".");
                return inputInt(message, max);
            }

            return Integer.parseInt(SCANNER.nextLine());
        } catch (NumberFormatException ex) {
            showInfo("El valor ingresado no es válido.");
            return inputInt(message, max);
        }
    }

    public static String inputString(Object message) {
        System.out.println(message);
        String text = SCANNER.nextLine().toUpperCase();
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

    public static void showInfo(Object message) {
        System.out.println(message);
    }

    public static int[][] keyChecker(String key, int length) {

        int[][] keyMatrix = new int[0][0];

        for (int i = 2; i < length || i < key.length(); i++) {
            if (key.length() == i * i) {

                keyMatrix = new int[i][i];
                int cont = 0;

                for (int j = 0; j < i; j++) {
                    for (int k = 0; k < i; k++) {
                        for (int m = 0; m < CHARACTERS_SPACE.length; m++) {

                            if (key.charAt(cont) == CHARACTERS_SPACE[m]) {
                                keyMatrix[k][j] = m;
                                cont++;
                                break;
                            }
                        }
                    }
                }
                break;
            }
        }

        int determinant = determinanteMatriz(keyMatrix, keyMatrix.length);

        if (determinant == 0 || determinant == 27) {
            showInfo("La llave no es valida, intente con una diferente");
            return keyChecker(inputString("Ingrese la llave:"), length);
        }


        return keyMatrix;
    }

    public static int determinanteMatriz(int x[][], int N) {
        int det = 0;
        switch (N) {
            case 0:
                break;
            case 2:
                det = ((x[0][0] * x[1][1]) - (x[1][0] * x[0][1]));
                break;
            case 3:
                det = ((x[0][0]) * (x[1][1]) * (x[2][2]) + (x[1][0]) * (x[2][1]) * (x[0][2]) + (x[2][0]) * (x[0][1]) * (x[1][2])) - ((x[2][0]) * (x[1][1]) * (x[0][2]) + (x[1][0]) * (x[0][1]) * (x[2][2]) + (x[0][0]) * (x[2][1]) * (x[1][2]));
                break;
            default:
                for (int z = 0; z < x.length; z++) {
                    det += (x[z][0] * adj(x, z, 0));
                }
        }
        return det;
    }

    public static int adj(int x[][], int i, int j) {
        int adjunto;
        int y[][] = new int[x.length - 1][x.length - 1];
        int m, n;
        for (int k = 0; k < y.length; k++) {
            if (k < i) {
                m = k;
            } else {
                m = k + 1;
            }
            for (int l = 0; l < y.length; l++) {
                if (l < j) {
                    n = l;
                } else {
                    n = l + 1;
                }
                y[k][l] = x[m][n];
            }
        }
        adjunto = (int) Math.pow(-1, i + j) * determinanteMatriz(y, y.length);
        return adjunto;
    }
}
