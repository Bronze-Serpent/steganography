import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Bitmask
{
    FIRST_ZERO(true, 0, (byte) 0),
    FIRST_ONE(true, 1, (byte) -128),
    FIRST_TWO(true, 2, (byte) -64),
    FIRST_THREE(true, 3, (byte) -32),
    FIRST_FOUR(true, 4, (byte) -16),
    FIRST_FIVE(true, 5, (byte) -8),
    FIRST_SIX(true, 6, (byte) -4),
    FIRST_SEVEN(true, 7, (byte) -2),
    FIRST_EIGHT(true, 8, (byte) -1),
    LAST_ZERO(false, 0, (byte) 0),
    LAST_ONE(false, 1, (byte) 1),
    LAST_TWO(false, 2, (byte) 3),
    LAST_THREE(false, 3, (byte) 7),
    LAST_FOUR(false, 4, (byte) 15),
    LAST_FIVE(false, 5, (byte) 31),
    LAST_SIX(false, 6, (byte) 63),
    LAST_SEVEN(false, 7, (byte) 127),
    LAST_EIGHT(false, 8, (byte) -1);


    private static final Map<Integer, Bitmask> intToFirstBitmask = Stream.of(values())
            .filter(Bitmask::isFirst)
            .collect(Collectors.toMap(Bitmask::getNum, e -> e));

    private static final Map<Integer, Bitmask> intToLastBitmask = Stream.of(values())
            .filter(e -> !e.isFirst())
            .collect(Collectors.toMap(Bitmask::getNum, e -> e));


    private final int num;
    private final byte mask;
    private final boolean isFirst;


    public byte getMask() { return mask; }

    public int getNum() { return num; }

    public boolean isFirst() { return isFirst; }

    public byte apply(byte num) { return (byte) (num & mask); }


    Bitmask(boolean isFirst, int num, byte mask)
    {
        this.isFirst = isFirst;
        this.num = num;
        this.mask = mask;
    }


    public static Bitmask fromNum(int num, boolean isFirst)
    {
        if (num < 0 || num > 8)
            throw new IllegalArgumentException("Incorrect value of argument 'qInByte'." +
                    " The value must be in the range [1, 8], but 'qInByte'=");

        return isFirst ? intToFirstBitmask.get(num) : intToLastBitmask.get(num);
    }
}
