/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.ssh;

import converter.Pixel;
import java.util.List;

/**
 *
 * @author Wouter
 */
public interface SshImageDecoderStrategy {
    public List<List<Pixel>> decodeImage(List<List<Pixel>> image);
    public List<List<Pixel>> encodeImage(List<List<Pixel>> image);
}
