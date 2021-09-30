/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mycompany.sshtobpmconverter.IPixel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wouter
 */
public class InterleafedDecoderStrategy implements SshImageDecoderStrategy{

    @Override
    public List<List<IPixel>> decodeImage(List<List<IPixel>> encodedImage) {
        List<List<IPixel>> decodedImage = new ArrayList<>();
        int nrOfRows = encodedImage.size();
        int nrOfColumns = encodedImage.get(0).size();
        for(int rowNr = 0; rowNr < nrOfRows; rowNr ++){
            decodedImage.add(new ArrayList<>());
            for(int colNr = 0; colNr < nrOfColumns; colNr++){
               decodedImage.get(rowNr).add(colNr,getPixelInEncodedImage(encodedImage, rowNr, colNr));
            }
        }
        return decodedImage;
    }
    
    @Override
    public List<List<IPixel>> encodeImage(List<List<IPixel>> decodedImage) {
        List<List<IPixel>> encodedImage = new ArrayList<>();
        int nrOfRows = decodedImage.size();
        int nrOfColumns = decodedImage.get(0).size();
        for(int rowNr = 0; rowNr < nrOfRows; rowNr ++){
            encodedImage.add(new ArrayList<>());
            for(int colNr = 0; colNr < nrOfColumns; colNr++){
               encodedImage.get(rowNr).add(colNr,getPixelDecodedImage(decodedImage, rowNr, colNr));
            }
        }
        return encodedImage;
    }

    private IPixel getPixelDecodedImage(List<List<IPixel>> encodedImage, int rowNr, int colNr) {
        boolean rowIsEven = rowNr % 2 == 0;
        int imgLength = encodedImage.get(0).size();
        
        int mapCols = 32;
        int mapRows = 8;
        
        int mapRowNr = rowNr % mapRows;
        int mapColNr = colNr % mapCols;
        int baseRowNr = rowNr - rowNr % mapRows;
        int baseColNr = colNr - colNr % mapCols;
        
        if(!rowIsEven){
            mapRowNr -= 1;
            baseColNr += imgLength;
        }
        baseColNr /= 2;
        
        if(! encodingMap.containsValue(new Point(mapRowNr, mapColNr))){
            mapColNr -= 2;
            baseColNr += 8;
        }
        Point offset = encodingMap.inverse().get(new Point(mapRowNr, mapColNr));
        int rowOffset = offset.x;
        int colOffset = offset.y;
        try{
        return encodedImage.get(baseRowNr + rowOffset).get(baseColNr + colOffset);
        } catch (Exception e){
            return null;
        }
    }
    
    /**
     * Get where to find each pixel in the encoded image. (when given where it will go on the unencoded image.
     * @param rowNr The row for which you need the pixel in the encoded image
     * @param colNr The column for which you need the pixel in the encoded image
     * @return A point representing (row, col) of where to find the pixel.
     */
    private IPixel getPixelInEncodedImage(List<List<IPixel>> image, int rowNr, int colNr) {
            //2 parts:  1. find the base, where to generally find the Pixel
            //          2. find where in the encodingMap to find the offset
            //          3. find the exact offset using the map
            //          4. base + offset is where to find the pixel.
                int imgLength = image.get(0).size();
                int mapRows = 8;
                int mapCols = 16;
                int mapRowNr = rowNr % mapRows;
                int mapColNr = colNr % mapCols;
                int baseRowNr = rowNr - mapRowNr;
                int baseColNr = colNr - mapColNr;
                
                baseColNr *= 2;
                if(baseColNr >= imgLength){
                    baseColNr -= imgLength;
                    baseRowNr += 1;
                }
                //the encodingMap is actually only 8 columns, but it's a repeat of the same nr but added 2
                // example: 
                // from "encodingMap.put(new Point(0,0), new Point(0,0));" we know that the complete map would include
                // "encodingMap.put(new Point(0,8), new Point(0,2));"
                if(!(mapColNr < (mapCols / 2))){
                    baseColNr += 2;
                    mapColNr -= 8;
                }
                Point offset = encodingMap.get(new Point(mapRowNr, mapColNr));
                int rowOffset = offset.x;
                int colOffset = offset.y;
                return image.get(baseRowNr + rowOffset).get(baseColNr + colOffset);
    }
    
    private static final BiMap<Point, Point> encodingMap = HashBiMap.create();
    static{
        encodingMap.put(new Point(0,0), new Point(0,0));
        encodingMap.put(new Point(0,1), new Point(0,4));
        encodingMap.put(new Point(0,2), new Point(0,8));
        encodingMap.put(new Point(0,3), new Point(0,12));
        encodingMap.put(new Point(0,4), new Point(0,16));
        encodingMap.put(new Point(0,5), new Point(0,20));
        encodingMap.put(new Point(0,6), new Point(0,24));
        encodingMap.put(new Point(0,7), new Point(0,28));
        
        encodingMap.put(new Point(1,0), new Point(2,0));
        encodingMap.put(new Point(1,1), new Point(2,4));
        encodingMap.put(new Point(1,2), new Point(2,8));
        encodingMap.put(new Point(1,3), new Point(2,12));
        encodingMap.put(new Point(1,4), new Point(2,16));
        encodingMap.put(new Point(1,5), new Point(2,20));
        encodingMap.put(new Point(1,6), new Point(2,24));
        encodingMap.put(new Point(1,7), new Point(2,28));
        
        encodingMap.put(new Point(2,0), new Point(0,17));
        encodingMap.put(new Point(2,1), new Point(0,21));
        encodingMap.put(new Point(2,2), new Point(0,25));
        encodingMap.put(new Point(2,3), new Point(0,29));
        encodingMap.put(new Point(2,4), new Point(0,1));
        encodingMap.put(new Point(2,5), new Point(0,5));
        encodingMap.put(new Point(2,6), new Point(0,9));
        encodingMap.put(new Point(2,7), new Point(0,13));
        
        encodingMap.put(new Point(3,0), new Point(2,17));
        encodingMap.put(new Point(3,1), new Point(2,21));
        encodingMap.put(new Point(3,2), new Point(2,25));
        encodingMap.put(new Point(3,3), new Point(2,29));
        encodingMap.put(new Point(3,4), new Point(2,1));
        encodingMap.put(new Point(3,5), new Point(2,5));
        encodingMap.put(new Point(3,6), new Point(2,9));
        encodingMap.put(new Point(3,7), new Point(2,13));
        
        encodingMap.put(new Point(4,0), new Point(4,16));
        encodingMap.put(new Point(4,1), new Point(4,20));
        encodingMap.put(new Point(4,2), new Point(4,24));
        encodingMap.put(new Point(4,3), new Point(4,28));
        encodingMap.put(new Point(4,4), new Point(4,0));
        encodingMap.put(new Point(4,5), new Point(4,4));
        encodingMap.put(new Point(4,6), new Point(4,8));
        encodingMap.put(new Point(4,7), new Point(4,12));
        
        encodingMap.put(new Point(5,0), new Point(6,16));
        encodingMap.put(new Point(5,1), new Point(6,20));
        encodingMap.put(new Point(5,2), new Point(6,24));
        encodingMap.put(new Point(5,3), new Point(6,28));
        encodingMap.put(new Point(5,4), new Point(6,0));
        encodingMap.put(new Point(5,5), new Point(6,4));
        encodingMap.put(new Point(5,6), new Point(6,8));
        encodingMap.put(new Point(5,7), new Point(6,12));
        
        encodingMap.put(new Point(6,0), new Point(4,1));
        encodingMap.put(new Point(6,1), new Point(4,5));
        encodingMap.put(new Point(6,2), new Point(4,9));
        encodingMap.put(new Point(6,3), new Point(4,13));
        encodingMap.put(new Point(6,4), new Point(4,17));
        encodingMap.put(new Point(6,5), new Point(4,21));
        encodingMap.put(new Point(6,6), new Point(4,25));
        encodingMap.put(new Point(6,7), new Point(4,29));   
        
        encodingMap.put(new Point(7,0), new Point(6,1));
        encodingMap.put(new Point(7,1), new Point(6,5));
        encodingMap.put(new Point(7,2), new Point(6,9));
        encodingMap.put(new Point(7,3), new Point(6,13));
        encodingMap.put(new Point(7,4), new Point(6,17));
        encodingMap.put(new Point(7,5), new Point(6,21));
        encodingMap.put(new Point(7,6), new Point(6,25));
        encodingMap.put(new Point(7,7), new Point(6,29));   
    }
}
