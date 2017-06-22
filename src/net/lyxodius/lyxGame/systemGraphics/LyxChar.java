package net.lyxodius.lyxGame.systemGraphics;

/**
 * Created by Lyxodius on 20.06.2017.
 */
public enum LyxChar {
    A(0, 'A'),
    B(1, 'B'),
    C(2, 'C'),
    D(3, 'D'),
    E(4, 'E'),
    F(5, 'F'),
    G(6, 'G'),
    H(7, 'H'),
    I(8, 'I'),
    J(9, 'J'),
    K(10, 'K'),
    L(11, 'L'),
    M(12, 'M'),
    N(13, 'N'),
    O(14, 'O'),
    P(15, 'P'),
    Q(16, 'Q'),
    R(17, 'R'),
    S(18, 'S'),
    T(19, 'T'),
    U(20, 'U'),
    V(21, 'V'),
    W(22, 'W'),
    X(23, 'X'),
    Y(24, 'Y'),
    Z(25, 'Z'),
    AE(26, 'Ä'),
    OE(27, 'Ö'),
    UE(28, 'Ü'),
    space(29, ' '),
    dot(30, '.'),
    comma(31, ','),
    a(32, 'a'),
    b(33, 'b'),
    c(34, 'c'),
    d(35, 'd'),
    e(36, 'e'),
    f(37, 'f'),
    g(38, 'g'),
    h(39, 'h'),
    i(40, 'i'),
    j(41, 'j'),
    k(42, 'k'),
    l(43, 'l'),
    m(44, 'm'),
    n(45, 'n'),
    o(46, 'o'),
    p(47, 'p'),
    q(48, 'q'),
    r(49, 'r'),
    s(50, 's'),
    t(51, 't'),
    u(52, 'u'),
    v(53, 'v'),
    w(54, 'w'),
    x(55, 'x'),
    y(56, 'y'),
    z(57, 'z'),
    ae(58, 'ä'),
    oe(59, 'ö'),
    ue(60, 'ü'),
    sz(61, 'ß'),
    exclamationMark(62, '!'),
    questionMark(63, '?'),
    one(64, '1'),
    two(65, '2'),
    three(66, '3'),
    four(67, '4'),
    five(68, '5'),
    six(69, '6'),
    seven(70, '7'),
    eight(71, '8'),
    nine(72, '9'),
    zero(73, '0'),
    plus(74, '+'),
    minus(75, '-'),
    times(76, '*'),
    divide(77, '/'),
    apostrophe(78, '\''),
    quotationMark(79, '"'),
    lessThan(80, '<'),
    moreThan(81, '>'),
    percent(82, '%'),
    and(83, '&'),
    leftBracket(84, '('),
    rightBracket(85, ')'),
    leftSquareBracket(86, '['),
    rightSquareBracket(87, ']'),
    leftCurlyBracket(88, '{'),
    rightCurlyBracket(89, '}'),
    equals(90, '='),
    underscore(91, '_'),
    number(92, '#'),
    tilde(93, '~'),
    circumflex(94, '^'),
    at(95, '@');

    private final int id;
    private final char value;

    LyxChar(int id, char c) {
        this.id = id;
        this.value = c;
    }

    public static int getId(char c) {
        for (LyxChar lyxChar : LyxChar.values()) {
            if (lyxChar.value == c) {
                return lyxChar.id;
            }
        }
        return -1;
    }
}
