import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */

public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> map = new HashMap<>();
        for (Course c : courses) {
            if (!map.containsKey(c.institution)) {
                map.put(c.institution, c.participants);
            } else {
                int temp = map.get(c.institution) + c.participants;
                map.put(c.institution, temp);
            }
        }
        Map<String, Integer> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> map = new HashMap<>();
        for (Course c : courses) {
            if (!map.containsKey(c.institution + "-" + c.subject)) {
                map.put(c.institution + "-" + c.subject, c.participants);
            } else {
                int temp = map.get(c.institution + "-" + c.subject) + c.participants;
                map.put(c.institution + "-" + c.subject, temp);
            }
        }
        Map<String, Integer> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> map = new HashMap<>();
        for (Course c : courses) {
            String[] instrus = c.instructors.split(", ");
            if (instrus.length > 1) {
                for (String s : instrus) {
                    if (!map.containsKey(s)) {
                        List<List<String>> list = new ArrayList<>();
                        List<String> inde = new ArrayList<>();
                        List<String> co = new ArrayList<>();
                        co.add(c.title);
                        list.add(inde);
                        list.add(co);
                        map.put(s, list);
                    } else {
                        int check = 0;
                        for (String s1 : map.get(s).get(1)) {
                            if (s1.equals(c.title)) {
                                check = 1;
                                break;
                            }
                        }
                        if (check == 0) map.get(s).get(1).add(c.title);
                    }
                }
            } else {
                if (!map.containsKey(c.instructors)) {
                    List<List<String>> list = new ArrayList<>();
                    List<String> inde = new ArrayList<>();
                    List<String> co = new ArrayList<>();
                    inde.add(c.title);
                    list.add(inde);
                    list.add(co);
                    map.put(c.instructors, list);
                } else {
                    int check = 0;
                    for (String s1 : map.get(c.instructors).get(0)) {
                        if (s1.equals(c.title)) {
                            check = 1;
                            break;
                        }
                    }
                    if (check == 0) map.get(c.instructors).get(0).add(c.title);
                }
            }
        }
        for (Map.Entry<String, List<List<String>>> s : map.entrySet()) {
            Collections.sort(s.getValue().get(0));
            Collections.sort(s.getValue().get(1));
        }
        return map;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        Set<String> list = new LinkedHashSet<>();
        List<String> list1 = new ArrayList<>();
        if (by.equals("hours")) {
            courses.sort(new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    return (int) ((o2.totalHours * 1000) - (o1.totalHours * 1000));
                }
            });
            int i = 0;
            while (list.size() != topK) {
                list.add(courses.get(i).title);
                i++;
            }
        } else if (by.equals("participants")) {
            courses.sort(new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    return (o2.participants) - (o1.participants);
                }
            });
            int i = 0;
            while (list.size() != topK) {
                list.add(courses.get(i).title);
                i++;
            }
        }
        list1.addAll(list);
        return list1;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        courseSubject = courseSubject.toLowerCase(Locale.ROOT);
        Set<String> list = new HashSet<>();
        for (Course c : courses) {
            String strings = c.subject.toLowerCase(Locale.ROOT);
            if (strings.indexOf(courseSubject) >= 0 && c.percentAudited >= percentAudited && c.totalHours <= totalCourseHours) {
                list.add(c.title);
            }
        }
        List<String> list1 = new ArrayList<>();
        list1.addAll(list);
        Collections.sort(list1);
        return list1;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        Map<String, double[]> map = new HashMap<>();
        for (Course c : courses) {
            if (!map.containsKey(c.number)) {
                double[] doubles = new double[4];
                doubles[0] = c.medianAge;
                doubles[1] = c.percentMale;
                doubles[2] = c.percentDegree;
                doubles[3] = 1;
                map.put(c.number, doubles);
            } else {
                double[] doubles = map.get(c.number);
                doubles[0] += c.medianAge;
                doubles[1] += c.percentMale;
                doubles[2] += c.percentDegree;
                doubles[3] += 1;
                map.put(c.number, doubles);
            }
        }
        Map<String, Double> doubleMap = new HashMap<>();
        for (Map.Entry<String, double[]> m : map.entrySet()) {
            double similarity = Math.pow((age - m.getValue()[0] / m.getValue()[3]), 2) + Math.pow((gender * 100 - m.getValue()[1] / m.getValue()[3]), 2) + Math.pow((isBachelorOrHigher * 100 - m.getValue()[2] / m.getValue()[3]), 2);
            doubleMap.put(m.getKey(), similarity);
        }
        Map<String, Double> result = doubleMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        List<String> list1 = new ArrayList<>();
        Set<String> results = new LinkedHashSet<>();
        for (Map.Entry<String, Double> m : result.entrySet()) {
            list1.add(m.getKey());
        }
        Map<String, Double> map1 = new LinkedHashMap<>();
        List<String> llist = new ArrayList<>();
        courses.sort((t1, t2) -> t2.launchDate.compareTo(t1.launchDate));
        for (String s : list1) {
            for (Course c : courses) {
                if (c.number.equals(s) && !results.contains(c.title)) {
                    llist.add(s);
                    results.add(c.title);
                    break;
                }
            }
            if (results.size() == 10) break;
        }
        int cnt = 0;
        for (String s : results) {
            map1.put(s, result.get(llist.get(cnt++)));
        }
        List<List<String>> re = new ArrayList<>();
        for (Map.Entry<String, Double> m : map1.entrySet()) {
            List<String> temp = new ArrayList<>();
            temp.add(m.getKey());
            temp.add(String.valueOf(m.getValue()));
            re.add(temp);
        }
        re.sort(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                if (o1.get(1).equals(o2.get(1))) {
                    return o1.get(0).compareTo(o2.get(0));
                } else {
                    return (int) (Math.floor(Double.parseDouble(o1.get(1)) / Double.parseDouble(o2.get(1))));
                }
            }
        });
        List<String> rre = new ArrayList<>();
        for (List<String> a : re) {
            rre.add(a.get(0));
        }
        return rre;
    }

}

class temp {
    String sub;
    double sim;

    public temp(String sub, double sim) {
        this.sub = sub;
        this.sim = sim;
    }
}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }
}