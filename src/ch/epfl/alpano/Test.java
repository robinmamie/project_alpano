package ch.epfl.alpano;

public class Test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println(Azimuth.toOctantString(Math.PI/180, "n", "e", "s", "w"));
        System.out.println(Math2.angularDistance(0, 359*Math.PI/180));

       
        
    }

}
