/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.comichigh;

import java.util.*;

/**
 *
 * @author Administrator
 */
public class ComicHighSite
{
    private static final String kBase = "zBCfA!c#e@-UHTOtLEaDPVbXYjgNrRQlyFWpZ$+no*qIhKSvuxGk~siwm%:dJ/M?";

    private static final int[] bbc = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095},
                            rTbl = new int[]{495, 510, 411, 433, 6, 425, 82, 889, 71, 422, 57, 445, 830, 260, 21, 649, 324, 249, 239, 632, 894, 691, 345, 363, 521, 465, 725, 122, 542, 519, 154, 219, 626, 567, 841, 481, 423, 757, 539, 553, 797, 606, 873, 532, 40, 974, 545, 973, 990, 353, 580, 796, 429, 272, 247, 117, 706, 77, 842, 557, 957, 880, 959, 28, 850, 380, 568, 161, 717, 131, 897, 309, 240, 551, 989, 9, 111, 326, 602, 38, 348, 844, 747, 360, 579, 175, 852, 251, 777, 480, 684, 174, 775, 106, 171, 926, 942, 170, 793, 89, 950, 890, 695, 328, 20, 468, 173, 546, 564, 791, 694, 451, 625, 735, 110, 23, 357, 213, 85, 610, 289, 27, 832, 507, 992, 909, 119, 872, 146, 185, 615, 798, 2, 108, 197, 756, 773, 22, 782, 264, 181, 884, 285, 454, 629, 596, 92, 19, 15, 367, 1, 877, 918, 734, 199, 486, 723, 783, 585, 840, 760, 443, 72, 708, 534, 437, 303, 322, 818, 399, 715, 977, 895, 538, 385, 109, 349, 987, 269, 273, 104, 915, 681, 617, 211, 620, 576, 645, 435, 961, 476, 808, 205, 656, 593, 675, 79, 716, 453, 204, 859, 306, 820, 920, 572, 641, 227, 980, 24, 976, 388, 287, 854, 8, 812, 200, 813, 616, 784, 340, 292, 788, 623, 408, 290, 555, 355, 496, 231, 701, 330, 635, 700, 392, 683, 144, 962, 347, 494, 279, 393, 662, 657, 382, 230, 646, 838, 491, 943, 903, 921, 126, 378, 595, 806, 748, 208, 436, 14, 746, 902, 624, 651, 207, 492, 670, 856, 182, 916, 999, 139, 256, 863, 742, 270, 373, 130, 899, 713, 52, 774, 604, 772, 698, 323, 676, 216, 351, 68, 857, 252, 537, 504, 931, 73, 640, 192, 944, 821, 952, 540, 924, 660, 305, 878, 609, 48, 685, 994, 366, 892, 607, 851, 642, 118, 379, 428, 36, 497, 177, 543, 673, 826, 839, 159, 427, 471, 690, 794, 265, 814, 463, 769, 482, 133, 666, 867, 368, 917, 120, 554, 209, 105, 226, 763, 978, 771, 834, 583, 705, 789, 244, 720, 295, 712, 96, 206, 87, 958, 586, 907, 659, 440, 603, 153, 223, 172, 424, 67, 377, 722, 141, 669, 115, 372, 262, 822, 299, 520, 935, 293, 829, 644, 668, 473, 84, 707, 384, 450, 236, 802, 370, 65, 78, 7, 312, 904, 544, 965, 512, 584, 296, 447, 985, 336, 922, 145, 342, 47, 614, 416, 319, 513, 787, 267, 352, 218, 655, 565, 648, 194, 807, 314, 905, 846, 228, 466, 37, 438, 412, 461, 654, 767, 121, 738, 809, 732, 548, 10, 728, 672, 304, 229, 845, 770, 335, 137, 589, 375, 790, 26, 843, 827, 25, 637, 276, 879, 941, 741, 506, 474, 928, 577, 991, 731, 459, 426, 811, 86, 594, 611, 971, 501, 43, 627, 338, 291, 41, 780, 203, 490, 39, 996, 74, 165, 891, 277, 246, 3, 960, 271, 143, 765, 46, 967, 925, 934, 280, 536, 90, 664, 819, 191, 156, 83, 588, 888, 792, 919, 686};

    // from http://stackoverflow.com/questions/2946067/what-is-the-java-equivalent-to-javascripts-string-fromcharcode
    private static String fromCharCode(int... codePoints)
    {
        StringBuilder builder = new StringBuilder(codePoints.length);
        for(int codePoint : codePoints)
        {
            builder.append(Character.toChars(codePoint));
        }
        return builder.toString();
    }

    private static String _sval_100(int i)
    {
        return(( i >= 100 ) ? "_" + i :
                ( i >= 10 ) ? "_0" + i :
                            "_00" + i);
    }

    public static String getIpntStr(int sm, int _npn, int _nsn, int _x, int _y)
    {
        String bx, by;
        int st;
        if(sm != 0 && _npn != 0)
        {
            st = (_npn + _nsn) % 17 * 30 + _x + sm;
            bx = _sval_100(rTbl[st % 510]);
            st = (_npn + _nsn + _x) % 17 * 30 + _y + sm + 13;
            by = _sval_100(rTbl[st % 510]);
        }
        else
        {
            bx = _sval_100(_x);
            by = _sval_100(_y);
        }
        return(bx + by);
    }

    public static void expand(String _src, int _key)
    {
        int i, j, _cs, _cd, _bis, _bit, _cbt, _ss, _pn;
        String _pst, _ks;
        String[] _tbl = new String[4096];
        int[] _byt = new int[4];
        int[] _ky = new int[4];
        StringBuffer _ret = new StringBuffer();

        _pst = "";
	j = _cs = _bis = _cd = _bit = _ss = 0;
	for (i = 0; i < 4; i++)
	{
            _ky[i] = (int)(_key & 0xFF);
            _key >>= 8;
	}

	_pn = 130;
	_cbt = 8;
	for(i = 0; i < _src.length(); i++)
	{
            _cs *= 64;
            _cs += kBase.indexOf(_src.charAt(i));
            _bis += 6;
            if (_bis >= 8)
            {
                _byt[j++] = (int)((_cs >> (_bis - 8)) & 0xFF);
                _bis -= 8;
                _cs = _cs & bbc[_bis];
                if (j == 4)
                {
                    for (j = 0; j < 4; j++)
                    {
                        _bit += 8;
                        _cd <<= 8;
                        _cd += _byt[j] ^ _ky[j];
                        if (_bit >= _cbt)
                        {
                            _ss = _cd >> _bit - _cbt & bbc[_cbt];
                            _bit -= _cbt;
                            _cd = _cd & bbc[_bit];
                            if (_ss != 128 && _ss != 129 && _ss <= _pn)
                            {
                                if (_ss < 128)
                                {
                                    _ks = fromCharCode(_ss);
                                }
                                else if (_ss == _pn)
                                {
                                    _ks = _pst + _pst.substring(0, 1);
                                }
                                else
                                {
                                    _ks = _tbl[_ss];
                                }
                                _ret.append(_ks);
                                if (_pst.length() > 0 && _ks.length() > 0)
                                {
                                    _tbl[_pn++] = _pst + _ks.substring(0, 1);
                                }
                                _pst = _ks;
                            }
                            if (_ss == 129 || _ss > _pn)
                            {
                                 break;
                            }
                            else if (_ss == 128 || _pn >= 4096)
                            {
                                 _pst = "";
                                 _pn = 130;
                                 _cbt = 8;
                            }
                            else if (_pn == 255 || _pn == 511 || _pn == 1023 || _pn == 2047)
                            {
                                 _cbt++;
                            }
                        }
                    }
                    if (j < 4)
                            break;
                    j = 0;
                }
            }
	}

        System.out.println("result: " + _ret);
    }
}
