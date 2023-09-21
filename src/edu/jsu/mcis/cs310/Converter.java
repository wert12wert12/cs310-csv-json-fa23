package edu.jsu.mcis.cs310;

import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        //String result = "{}"; // default return value; replace later!
        
        JsonObject Obj = new JsonObject();
        
        JsonArray ProdNums = new JsonArray();
        
        JsonArray ColHeadings = new JsonArray();
        
        JsonArray Data = new JsonArray();
        
        try {
            
            String[] Lines = csvString.split("\n");
            
            // Convert lines to json arrays.
            
            // remove first array as column headers
            
            //
            
            for (int i = 0; i < Lines.length; i++) {
                // "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
                // ->
                // ["6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"]
                var LineData = Lines[i].replace("\"", "").split(",");
                
                if (i == 0) {
                    for (int d = 0; d < LineData.length; d++) {
                        ColHeadings.add(LineData[d]);
                    }
                    
                    continue;
                }
                
                ProdNums.add(LineData[0]);
                
                JsonArray DataArr = new JsonArray();
                
                for (int d = 1; d < LineData.length; d++) {
                    DataArr.add(LineData[d]);
                }
                
                Data.add(DataArr);
            }
            
            Obj.put("ProdNums", ProdNums);
            Obj.put("ColHeadings", ColHeadings);
            Obj.put("Data", Data); 
            
            
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return Obj.toJson();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
       
        StringWriter writer = new StringWriter();
        
        CSVWriter csvWriter = new CSVWriter(writer);
        
        try {
            
            List<String[]> data = new ArrayList<String[]>();
            
            JsonObject parsedData = Jsoner.deserialize(jsonString, new JsonObject());
            
            JsonArray ColHeadings = (JsonArray) parsedData.get("ColHeadings");
            JsonArray ProdNums = (JsonArray) parsedData.get("ProdNums");
            JsonArray Datas = (JsonArray) parsedData.get("Data");
            
            // add column headers.
            String columnName = ColHeadings.toString();
            String[] columnNameArray = columnName.substring(1, columnName.length() - 1).split(", ");
            data.add(columnNameArray);
            //adding production numbers
            String productionNum = ProdNums.toString();
            String[] proNumArray = productionNum.substring(1, productionNum.length() - 1).split(", ");
            
            
            //adding the rest of the data
            String datas = Datas.toString();
            String[] datasArray = datas.substring(1, datas.length() - 1).split("],");
           
            List<String[]> allTogether = new ArrayList();
            
            // loop over prodnums
            // grab data from prodnums and episode data.
            for (int i = 0; i < proNumArray.length; i++) {
                List<String> whatever = new ArrayList();
                
                String ProdNum = proNumArray[i];
                String[] DataData = datasArray[i].substring(2, datasArray[i].length() - 1).split(", ");
                
                whatever.add(ProdNum);
                whatever.addAll(Arrays.asList(DataData));
                String[] arr = whatever.toArray(new String[0]);
                allTogether.add(arr);
            }
            
            data.addAll(allTogether);
            
            csvWriter.writeAll(data);
            csvWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return writer.toString();
        
    }
    
}
