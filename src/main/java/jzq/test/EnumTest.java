package jzq.test;

public class EnumTest {
    public static void main(String[] args) {

        Day day =Day.MONDAY;
        System.out.println(day.ordinal());
        switch (day){
            case MONDAY:
                System.out.println(day);
                break;
            case SUNDAY:
                System.out.println(day);
                break;
            default:
                break;
        }
    }


}


enum Day{
    MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY, SUNDAY;
}
