/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import com.mycompany.sshtobpmconverter.*;
import util.ByteUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import converter.ImageFileWrapper;
import converter.Pixel;
import image.ImgComponent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import util.PrintUtil;

/**
 *
 * @author Wouter This table links an rgb code to every byte (which byte is
 * determined by it's place in the table).
 */
public class SshColorTable implements ImgComponent {

    private long offset;

    private int nrOfTableEntries = 256;
    private int singleTableEntrySize = 3;
    private int sshTableDelimiterSize = 1;
    private final byte sshTableDelimiter = (byte) 0x80;

    private BiMap<Integer, Pixel> colorMap = HashBiMap.create();

    public SshColorTable(RandomAccessFile sshFile, long offset) throws IOException {
        this.offset = offset;
        sshFile.seek(offset);
        byte[] bgrValue = new byte[singleTableEntrySize];
        for (int i = 0; i < nrOfTableEntries; i++) {
            int pixelCode = getPixelCodeFromTableIndexNr(i);
            sshFile.read(bgrValue);


            try {
                colorMap.put(pixelCode, Pixel.createPixelFromBGR(bgrValue));
            } catch (Exception e) {
                int s = 1;
            }
            sshFile.skipBytes(sshTableDelimiterSize);
        }
    }

    public SshColorTable(ImageFileWrapper wrapper) throws IOException {
        int width = wrapper.getImgWidth();
        List<List<Pixel>> image = wrapper.getImage();
        int colorNr = 0;
        for (List<Pixel> row : image) {
            for (Pixel pixel : row) {
                if (!colorMap.containsValue(pixel)) {
                    colorMap.put(colorNr, pixel);
                    colorNr ++;
                }
            }
        }
    }

    /**
     * Get the pixelcode hex-code of the byte(as an int from 0-256) that is
     * linked to the color or entry in the table on position [index]
     *
     * PixelCode refers to the character, whose pixel rgb value will be
     * determined by this table
     *
     * @param index this is the position in the table
     * @return the Hex-code that is linked to the value in this table
     */
    private int getPixelCodeFromTableIndexNr(int index) {
        return ByteUtil.simulateSwitching4th5thBit(index) % 256;
    }

    private int getTableIndexFromPixelCode(int pixelCode) {
        return ByteUtil.simulateSwitching4th5thBit(pixelCode) % 256;
    }

    public byte getByteFromPixel(Pixel pixel) {
        int result = colorMap.inverse().get(pixel);
        return (byte) result;
    }

    public Pixel getPixelfromPixelCode(int charCode) {
        return colorMap.get(charCode);
    }

    @Override
    public long getSize() {
        return nrOfTableEntries * (singleTableEntrySize + sshTableDelimiterSize);
    }

    /**
     * Creates the table that ssh uses to determine the color Note: rgb-value is
     * noted as "brg"
     *
     * @param colorTable
     * @return
     * @throws IOException
     * @throws DecoderException
     */
    public byte[] createByteArray() {
        byte[] currentPixelBGR = new byte[singleTableEntrySize];
        int singleTableEntryTotalSize = this.singleTableEntrySize + sshTableDelimiterSize;
        byte[] result = new byte[nrOfTableEntries * (singleTableEntryTotalSize)];
        int resultArrayIndex = 0;
        for (int tableIndex = 0; tableIndex < nrOfTableEntries; tableIndex++) {
            //convert to ssh-table order
            int pixelCode = getPixelCodeFromTableIndexNr(tableIndex);
            if (colorMap.containsKey(pixelCode)) {
                Pixel pixel = colorMap.get(pixelCode);
                currentPixelBGR = pixel.getBGRValue();
            } else {
                currentPixelBGR = new byte[3];
            }
            System.arraycopy(currentPixelBGR, 0, result, resultArrayIndex, currentPixelBGR.length);
            resultArrayIndex += currentPixelBGR.length;
            result[resultArrayIndex] = sshTableDelimiter;
            resultArrayIndex++;
        }
        return result;
    }

    @Override
    public void printFormatted() {
        System.out.println("--COLOR TABLE--");
        System.out.println(PrintUtil.insert(PrintUtil.toHexString(false, createByteArray()), "\n", 16 * 3));
    }

    @Override
    public long getPositionOffset() {
        return offset;
    }

    @Override
    public void writeToFile(OutputStream os) throws IOException {
        os.write(createByteArray());
    }
}
