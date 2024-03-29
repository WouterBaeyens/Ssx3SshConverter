/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import com.mycompany.sshtobpmconverter.IPixel;
import image.ssh2.imageheader.strategies.ByteToPixelStrategy;

import java.util.List;

/**
 *
 * @author Wouter
 */
public interface SshImageDecoderStrategy {
    public List<List<IPixel>> decodeImage(List<List<IPixel>> image, ByteToPixelStrategy byteToPixelStrategy);

    public List<List<IPixel>> encodeImage(List<List<IPixel>> image);
}
