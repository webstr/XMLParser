package Parsers;

import java.util.Comparator;

/**
 * class compare two Guns classes based
 * on their effective distance attribute
 */
public class GunComparator implements Comparator<Gun.Item> {
    @Override
    public int compare(Gun.Item g1, Gun.Item g2){
        short d1 = g1.getTTC().getEffectiveDistance();
        short d2 = g2.getTTC().getEffectiveDistance();
        if(d1 > d2)
            return 1;

        else if(d2 > d1)
            return -1;

        return 0;
    }
}
