import java.util.Arrays;
import java.util.Scanner;

public class Hill {

    public static final char[] CHARACTERS_SPACE = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' '};
    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        char[] message = inputString("Ingrese un mensaje a cifrar:").toCharArray();
//        int separator = inputInt("Ingrese el separador: ", message.length);
        int[][] key = keyChecker(inputString("Ingrese la llave:"), message.length);

        for (int[] c : key){
            System.out.println(Arrays.toString(c));
        }

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

    public static int[][] keyChecker(String key, int length){

        int[][] keyMatrix = new int[0][0];

        for (int i = 2; i < length || i < key.length(); i++){
            if (key.length() == i*i){

                keyMatrix = new int[i][i];
                int cont = 0;

                for (int j = 0; j < i; j++){
                    for (int k = 0; k < i; k++){
                        for (int m = 0; m < CHARACTERS_SPACE.length; m++){

                            if (key.charAt(cont) == CHARACTERS_SPACE[m]){
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

        int determinant = determinanteMatriz(keyMatrix,keyMatrix.length);

        if (determinant == 0 || determinant == 27){
            showInfo("La llave no es valida, intente con una diferente");
            return keyChecker(inputString("Ingrese la llave:"), length);
        }


        return keyMatrix;
    }

    public static int determinanteMatriz(int x[][], int N){
        int det=0;
        switch(N){
            case 0:
                break;
            case 2:
                det=((x[0][0]*x[1][1])-(x[1][0]*x[0][1]));
                break;
            case 3:
                det=((x[0][0])*(x[1][1])*(x[2][2])+(x[1][0])*(x[2][1])*(x[0][2])+(x[2][0])*(x[0][1])*(x[1][2]))-((x[2][0])*(x[1][1])*(x[0][2])+(x[1][0])*(x[0][1])*(x[2][2])+(x[0][0])*(x[2][1])*(x[1][2]));
                break;
            default:
                for(int z=0;z<x.length;z++){
                    det+=(x[z][0]*adj(x,z,0));
                }
        }
        return det;
    }
    public static int adj(int x[][], int i, int j){
        int adjunto;
        int y[][]=new int[x.length-1][x.length-1];
        int m,n;
        for(int k=0;k<y.length;k++){
            if(k<i){
                m=k;
            }
            else{
                m=k+1;
            }
            for(int l=0;l<y.length;l++){
                if(l<j){
                    n=l;
                }
                else{
                    n=l+1;
                }
                y[k][l]=x[m][n];
            }
        }
        adjunto=(int)Math.pow(-1,i+j)*determinanteMatriz(y, y.length);
        return adjunto;
    }
}
