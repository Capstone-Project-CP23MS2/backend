package sit.cp23ms2.sportconnect;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMain {
    public static void main(String[] args) {
        // Stream from Array
        String[] array = new String[]{"a", "b", "c", "d", "e"};
        Stream<String> streamOfArray = Arrays.stream(array);

        Set<String> strings = new HashSet<>() {
        };
        strings.add("A");
        strings.add("B");
        strings.add("C");
        strings.add("D");


    // Stream from Collection
        List<String> collection = Arrays.asList(array);
        Stream<String> streamOfCollection = collection.stream();

    // User Stream's 'of' method
        Stream<String> stream = Stream.of("a", "b", "c", "d", "e");

        Set<String> test = strings.stream().map(s -> {
            s = s + " yo";
            return s;
        }).collect(Collectors.toSet());
        System.out.println(test);
        System.out.println(streamOfCollection);
        System.out.println(stream);
    }
}
