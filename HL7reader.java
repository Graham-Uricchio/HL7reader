import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HL7reader {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        int passes = 0;
        int sub = 0;
        boolean subBool = false;
        boolean doAgain = true;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter filepath of file to read");
        String inFile = input.next();
        while (doAgain){
            FileInputStream fis = new FileInputStream(inFile);
            System.out.println("Enter the segment you want to see:");
            String segment = input.next();
            segment = segment.toUpperCase();
            System.out.println("Enter the field you want to see:");
            int field = input.nextInt();
            if(segment.equalsIgnoreCase("MSH")){field--;}
            System.out.println("Would you like yo check a subfield? (Y/N)");
            String subField = input.next();
            if (subField.equalsIgnoreCase("Y")){
                System.out.println("Which subfield would you like to check? ");
                sub = input.nextInt();
                sub = sub - 1;
                subBool = true;
            }
            System.out.println("");
            Map<String, Integer> distro = new HashMap<>();
            String patName = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts[0].equalsIgnoreCase("MSH")) {passes++;}
                    if (parts[0].equalsIgnoreCase(segment)){
                        if (subBool){
                           String[] subParts = parts[field].split("\\^");
                           if (distro.containsKey(subParts[sub])){
                               int count = distro.get(subParts[sub]);
                               count++;
                               distro.put(subParts[sub], count);
                           }
                           else {distro.put(subParts[sub], 1);}
                        }
                        else {  
                            if (parts.length > field){
                                if (distro.containsKey(parts[field])){
                                int count = distro.get(parts[field]);
                                count++;
                                distro.put(parts[field], count); 
                                }
                                else {distro.put(parts[field], 1);}
                            }
                            
                        }
                    }
                } //end file while
                //sort the distro map
                Map<String, Integer> distroSorted = 
                    distro.entrySet().stream()
                    .sorted(Entry.comparingByValue())
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                              (e1, e2) -> e1, LinkedHashMap::new));
                //count number of messsages
                if (segment.equalsIgnoreCase("MSH")){
                    field++;
                }
                //print distribution
                System.out.println("========" + segment + "-" + field + "========");
                distroSorted.entrySet().forEach((entry) -> {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    System.out.println(value + "\t\t" + key);
                });
                //close streams
                System.out.println("");
                br.close();
                fis.close();
            } // end try 
            //print results
            System.out.println("");
            System.out.println("Number of messages analyzed: " + passes);
            System.out.println("Would you like to run again? (Y/N) ");
            sub = 0;
            subBool = false;
            String retry = input.next();
            retry = retry.toUpperCase();
            if (retry.equals("Y")){ doAgain = true; passes=0;}
            else{doAgain = false;}
         }//end doAgain while
    }//end main
}//end class
