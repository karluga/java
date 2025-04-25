import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("companies.csv"));
        System.out.println(reader.readLine());

        // List<String> companies = reader.lines()
        //     .map(data -> data.split(","))
        //     // .filter(data -> data[3].equals("Cybersecurity"))
        //     // .filter(data -> Integer.parseInt(data[3]) > 2020)
        //     // Average employee count in each category
        //     .collect(Collectors.groupingBy(data -> data[3], Collectors.averagingInt(data -> Integer.parseInt(data[2]))))
        //     .map(data -> "Companies" + data[0] + "Category" + data[3])
        //     .collect(Collectors.toList());

        List<String> companies = reader.lines()
            .map(data -> data.split(","))
            .collect(Collectors.groupingBy(data -> data[3], Collectors.averagingInt(data -> Integer.parseInt(data[2]))))
            .entrySet()
            .stream()
            .map(entry -> "Category: " + entry.getKey() + ", Average Employees: " + entry.getValue())
            .collect(Collectors.toList());


        companies.forEach(System.out::println);
    }
}