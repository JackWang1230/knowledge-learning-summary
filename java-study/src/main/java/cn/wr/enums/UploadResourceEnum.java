package cn.wr.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
public enum UploadResourceEnum {

    File("file", "filePath"),
    Photo("photo", "photoPath"),
    Video("video", "videoPath");

    private final String name;
    private final String path;

    UploadResourceEnum(String name, String path) {
        this.name = name;
        this.path = path;
    }
    private static final Set<UploadResourceEnum> ALL =EnumSet.allOf(UploadResourceEnum.class);

    private static UploadResourceEnum getEnumByName(String name){

        if (null == name){
            return null;
        }
        return ALL.stream()
                .filter(o->o.name.equals(name))
                .findAny()
                .orElseThrow(()->new RuntimeException("this enum value is error:"+name));

    }

}
