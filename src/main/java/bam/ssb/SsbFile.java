package bam.ssb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SsbFile {

    final ByteBuffer rawDecompressedSsbData;

    public SsbFile(final ByteBuffer buffer){
        final List<SsbComponent> ssbComponents = new ArrayList<>();
        while (true){
            final SsbComponent component = new SsbComponent(buffer);
            ssbComponents.add(component);
            if (component.isLastComponentOfFile()){
                this.rawDecompressedSsbData = getMergedBuffer(ssbComponents);
                break;
            }
        }
    }

    public ByteBuffer getRawDecompressedSsbData(){
        return rawDecompressedSsbData;
    }

    private ByteBuffer getMergedBuffer(final List<SsbComponent> components){
        int totalSize = components.stream()
                .map(SsbComponent::getSize)
                .mapToInt(Integer::intValue)
                .sum();
        ByteBuffer mergedBuffer  = ByteBuffer.allocate(totalSize);
        components.forEach(ssbComponent -> mergedBuffer.put(ssbComponent.getDecompressedSubfile()));
        mergedBuffer.position(0);
        return mergedBuffer;
    }
}
