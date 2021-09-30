/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import com.mycompany.sshtobpmconverter.IPixel;

import java.util.List;

/**
 *
 * @author Wouter
 */
public class NoneDecoderStrategy implements SshImageDecoderStrategy{

    @Override
    public List<List<IPixel>> decodeImage(List<List<IPixel>> image) {
        return image;
    }

    @Override
    public List<List<IPixel>> encodeImage(List<List<IPixel>> image) {
        return image;
    }
    
}
