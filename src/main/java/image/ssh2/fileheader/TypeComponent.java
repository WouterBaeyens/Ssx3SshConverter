package image.ssh2.fileheader;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public interface TypeComponent<T extends Enum<T> & ComponentType> {

    /** This is just used as a way to get around type erasure.
     * Obviously the return value will be T.class
     */
    Class<T> getTypeClass();
    byte[] getBytesBE();

    default Optional<T> getType(){
        String readableData = getTypeClass().getEnumConstants()[0].toReadable().apply(getBytesBE());
        Optional<T> matchingType = Arrays.stream(getTypeClass().getEnumConstants())
                .filter(fileType -> fileType.getReadableValue().equals(readableData))
                .findAny();
        if(matchingType.isEmpty()){
            LoggerFactory.getLogger(TypeComponent.class).warn("{} is not a known type of{}!", readableData, getTypeClass());
        }
        return matchingType;
    }

    default String getTypeInfo() {
        String readableData = getTypeClass().getEnumConstants()[0].toReadable().apply(getBytesBE());
        return getType()
                .map(fileType -> fileType + "(" + fileType.getReadableValue() + ")")
                .orElseGet(() -> "Unknown type (" + readableData + ")");
    }
}


