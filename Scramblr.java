import BasicIO.*;

/**
 * Scramblr
 */
public class Scramblr {

    private static final int MAX_Line = 500;

    ASCIIDataFile textIN, encryptIN;
    ASCIIOutputFile textOUT, encryptOUT;

    char[] hexSymbols = "0123456789ABCDEF".toCharArray();

    public Scramblr(){
        
    }

    //"Set File to Encrypt"
    public void setFTEnc(String path){
        textIN = new ASCIIDataFile(path);
    }

    //"Set File to Decrypt"
    public void setFTDec(String path){
        encryptIN = new ASCIIDataFile(path);
    }

    public void Encrypt(String path){
        String key;
        int[] keyVals = new int[12];
        String[] lines = new String[MAX_Line];
        int maxLineSize = 0;
        char[] temp;
        char[][] gridIN;
        int i = 0, k = 0;


        while (!textIN.isEOF()){
            lines[i] = textIN.readLine();            
            if (lines[i] != null && lines[i].length() > maxLineSize)
                maxLineSize = lines[i].length();  
                
            i++;
        }

        //create grids
        gridIN = new char[i - 1][maxLineSize];

        for (String s: lines) {
            if (s != null){
                temp = s.toCharArray();
                for (int j = 0; j < s.length(); j++){
                    gridIN[k][j] = temp[j];
                }
                k++;
            }
        }

        //Check lines for debugging
        printArray(gridIN);

        //encrypt the data
        key = generateKey();

        // This step breaks down the key and determines the keyValue
        // the keyValue is the total sum of the products of the
        // paired hexidecimals between the colons in the key
        // this value is then reduced under specifications in the "reduceVal()" function

        // The value determines the numbetr of itterations the encryption completes in its
        // cycle, as well as the value in which the char int values are increased

        int index = 0, keyValue = 0;    
        
        for (int j = 0; j < 29 ; j += 5){
            keyVals[index] = hexToInt(""+key.charAt(j)+key.charAt(j+1));
            keyVals[index + 1] = hexToInt(""+key.charAt(j+2)+key.charAt(j+3));

            //debug
            //log("keyVals[" + index + "]: " + keyVals[index] + " keyVals[" + (index+1) + "]: " + keyVals[index + 1]);
           
            keyValue += keyVals[index] * keyVals[index+1];    
            index+=2;    
        }
        log("KeyValue: "+keyValue);
        //reduce key value //TODO this may affect decryption algorithm, or same statements must be present
        keyValue = reduceVal(keyValue);

        //ENCRYPTION CYCLE
        for (int itr = 0; itr < keyValue; itr++){
            for (int j = 0; j < keyVals.length; j += 2){
                gridIN = swapAll(gridIN, keyVals[j], keyVals[j+1]);
                gridIN = incrementAll(gridIN, keyValue, false);
            }
        }

        System.out.println("data encrypted saved to: " + path + ".txt");
        
        //write to file
        encryptOUT = new ASCIIOutputFile(path + ".txt");
        encryptOUT.writeLine(key);
        for (int x = 0; x < gridIN.length; x++){            
            encryptOUT.writeLine(charArrToString(gridIN[x]));
        }
    } //Encrypt

    public void Decrypt(String path){
        textOUT = new ASCIIOutputFile(path + ".txt");
        char[][] gridIN;
        int[] keyVals = new int[12]; //values of each hexidecimal in key >> [0] [1] / [2] [3] are pairs
        int lineCount = 0, longestLine = 0;
        String[] temp = new String[500];
        String key = encryptIN.readLine();

        log("KEY: " + key + "\n");

        while (true){
            if (encryptIN.isEOF()) break;
            temp[lineCount] = encryptIN.readLine();

            log("LINE: "+temp[lineCount]); // DEBUG

            if (temp[lineCount] != null && temp[lineCount].length() > longestLine)
                longestLine = temp[lineCount].length();
            
            lineCount++;
        }

        //create grid
        gridIN = new char[lineCount - 1][longestLine];
        for (int i = 0; i < gridIN.length; i++){
            for(int j = 0; j < gridIN[i].length; j++){
                gridIN[i][j] = temp[i].charAt(j);
            }
        }

        //debug
        for (int i = 0; i < gridIN.length; i++){
            for(int j = 0; j < gridIN[i].length; j++){
                System.out.print(gridIN[i][j]);
            }
            System.out.print("\n");
        }


        int index = 0, keyValue = 0;    
        
        for (int j = 0; j < 29 ; j += 5){
            keyVals[index] = hexToInt(""+key.charAt(j)+key.charAt(j+1));
            keyVals[index + 1] = hexToInt(""+key.charAt(j+2)+key.charAt(j+3));

            //debug
            //log("keyVals[" + index + "]: " + keyVals[index] + " keyVals[" + (index+1) + "]: " + keyVals[index + 1]);
           
            keyValue += keyVals[index] * keyVals[index+1];    
            index+=2;    
        }

        log("KeyValue: "+keyValue);
        keyValue = reduceVal(keyValue);

        //DECRYPTION CYCLE
        for (int itr = 0; itr < keyValue; itr++){
            for (int j = keyVals.length - 1; j > 0 ; j -= 2){
                gridIN = incrementAll(gridIN, keyValue, true);
                if (j >= 2)
                gridIN = swapAll(gridIN, keyVals[j], keyVals[j-1]);
            }
        }

        //write to text file
        for (int i = 0; i < gridIN.length; i++){
            textOUT.writeLine(charArrToString(gridIN[i]));
        }

    }

    private char[][] swapAll(char[][] grid, int a, int b){

        char cOne = (char) a;
        char cTwo = (char) b;

       // log("SWAP>>> " + cOne + " & " + cTwo);

        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++){
                if (grid[i][j] == cOne){
                    //log(grid[i][j] + "FOUND");
                    grid[i][j] = cTwo;
                    //log("(" + i + ", " + j + ") " + "= " + grid[i][j]);
                }
                else if (grid[i][j] == cTwo){
                    //log(grid[i][j] + "FOUND");
                    grid[i][j] = cOne;
                    //log("(" + i + ", " + j + ") " + "= " + grid[i][j]);
                }
            }
        }        
        //check grid (debug)
        //System.out.println("== CURRENT GRID ==");
        //printArray(grid);

        return grid; 
    }

    private char[][] incrementAll(char[][] grid, int val, boolean negative){
        int temp;
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++){
                temp = (int)grid[i][j];

                if (negative)
                    temp  = (temp - val);
                else
                    temp = (temp + val);

                if (temp > 124)
                    temp = temp - 92;

                if (temp < 32)
                    temp = temp + 92;

                grid[i][j] = (char) (temp);
            }
        }
        return grid;
    }

    private String generateKey(){
        String result = "";

        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 2; j++){
                result += intToHex(randomInt(32, 124));                
            }
            if (i < 5)   
                    result += ":";
            
        }
        //log(result); //debugging
        return result;
    }

    private int getKeyValue(String key){
        return 0;
    }

    public int hexToInt(String h){
        int value = 0;
        char[] hex = h.toCharArray();      
        
            for (int i = 0; i < 16; i++){
                if (hex[0] == hexSymbols[i])
                    value = 16 * i;
                
            }
            for (int i = 0; i < 16; i++){
                if (hex[1] == hexSymbols[i])
                    value += i;                
            }
        
            return value;

    }

    private int reduceVal(int n){
        if (n >= 124){

            if (n >= 1000 && n <= 10000){
            n = n / 1000;
            }
            else if (n >= 10000 && n <= 100000){
                n = n / 10000;
            }
            else if (n > 100000){
                n = n / 100000; 
            } 
            else
                n = n - 124;
        }
        return n;
    }

    private String charArrToString(char[] c){
        String result = "";
        for (char d : c) {
            result += d;
        }
        return result;
    }
    
    public String intToHex(int n){
        
        char[] hex = new char[2];
        hex[0] = hexSymbols[(n / 16)];
        hex[1] = hexSymbols[(n % 16)];

        return ""+ hex[0] + hex[1];
    }

    private int randomInt(int min, int max){
        return (int) ((max-min)*Math.random()) + min;
    }

    private void printArray(char[][] arr){
        for (int i = 0; i < arr.length; i++){
            for (int j = 0; j < arr[i].length; j++){
                System.out.print(arr[i][j]);
            }
            System.out.print("\n");
        }
    }

    private void log(String s){
        System.out.println(s);
    }

}