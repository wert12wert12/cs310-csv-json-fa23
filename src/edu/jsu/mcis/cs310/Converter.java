package edu.jsu.mcis.cs310;

import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
        
        String jsonString = ""; // default return value; replace later!
        
        JsonArray prodNums = new JsonArray();
        
        JsonArray ColHeadings = new JsonArray();
        
        JsonArray records = new JsonArray();
        
        LinkedHashMap<String, JsonArray> data = new LinkedHashMap<>();
        
        
        try {
            
            //Newest Attempt
            
                CSVReader reader = new CSVReader(new StringReader(csvString));
                List<String[]> full = reader.readAll();
                Iterator<String[]> iterator = full.iterator();
                
                
                // Puts the Headers in
                if (iterator.hasNext()) {
                    String[] col = iterator.next();
                    
                    for (int i = 0; i < col.length; i++)
                        ColHeadings.add(col[i]);
                }
                
                //Puts the Product numbers in Object while putting rest of info in the other array
                
                while (iterator.hasNext()) {
                    
                    String[] theData = iterator.next();
                    
                    prodNums.add(theData[0]);
                    
                    JsonArray moreData = new JsonArray();
                    
                    for (int i = 1; i < theData.length; i++) {
                        String spot = theData[i];
                        //System.err.println(spot);
                        if (spot.matches("[0-9]+")) {
                            moreData.add(Integer.valueOf(spot));
                        } else {
                            moreData.add(theData[i]);
                        }
                    }
                        
                    records.add(moreData);
                }
            
                data.put("ProdNums", prodNums);
                data.put("ColHeadings", ColHeadings);
                data.put("Data", records);
            
                jsonString = Jsoner.serialize(data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return jsonString;
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
       
        String csvString = "";
        
        JsonObject json = Jsoner.deserialize(jsonString, new JsonObject());
        
        String[] ColHeadings = (String[]) ((JsonArray) json.get("ColHeadings")).toArray(new String[0]);
        String[] ProdNums = (String[]) ((JsonArray) json.get("ProdNums")).toArray(new String[0]);
        JsonArray Data = (JsonArray) json.get("Data");
        
        Iterator DataIt = Data.iterator();
        
        try {
            
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            
            // write colheadings
            csvWriter.writeNext(ColHeadings);
            
            for (int i = 0; i < ProdNums.length; i++) {
                var addZero = false;
               
                List<String> lineData = new ArrayList();
                
                lineData.add(ProdNums[i]);
                
                JsonArray d = (JsonArray) Data.get(i);
                    
                for (int j = 0; j < d.size(); j++) {
                    var dString = d.getString(j);
                   
                    if ((dString.matches("[0-9]+")) && (Integer.valueOf(dString) < 10) && (addZero)) {
                        dString = "0" + dString;
                    } else if ((dString.matches("[0-9]+")) && (!addZero)) {
                        addZero = true;
                    }
                    
                    lineData.add(dString);
                }
                
                csvWriter.writeNext(lineData.toArray(new String[0]));
            }

            // convert data to csvString format.
            csvString = writer.toString().trim();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return csvString;
        
    }
    
}
