package gogo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SelfUtils {
    public static void main(String[] args) {
        List<Self> list = init();
        /*Map<Integer, List<String>> collect = list.stream().collect(Collectors.groupingBy(Self::getAge, Collectors.mapping(Self::getName, Collectors.toList())));
        System.out.println(collect);*/
        Self self = list.stream().filter(e -> StringUtils.equals("1", e.getName())).findFirst().orElse(null);
        System.out.println(self);

    }

    private static List<Self> init() {
        List<Self> list = new ArrayList<>();
        list.add(new Self(1L, "A", 20));
        list.add(new Self(2L, "B", 20));
        list.add(new Self(3L, "C", 21));
        list.add(new Self(4L, "D", 22));
        list.add(new Self(5L, "E", 23));
        list.add(new Self(6L, "F", 24));
        list.add(new Self(7L, "G", 25));
        list.add(new Self(8L, "H", 26));
        list.add(new Self(9L, "J", 27));
        list.add(new Self(10L, "K", 28));
        return list;
    }

    @Data
    static class Self {
        Long id;
        String name;
        Integer age;

        private Self(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }
}
