/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.net.*;
import java.util.*;

import anonscanlations.downloader.ui.*;
import anonscanlations.downloader.yahoocomic.*;
import anonscanlations.downloader.ganganonline.*;
import anonscanlations.downloader.comichigh.*;
import anonscanlations.downloader.sunday.*;

/**
 *
 * @author /a/non
 */
public class Downloader
{
    public static final String ABOUT = "<html>About Online Manga Downloader<br/>"
            + "PCViewer decrypter and Club Sunday scraper made by Nagato<br/>"
            + "GUI made by /a/nonymous scanlations<br/>"
            + "<a href=\"http://anonscanlations.blogspot.com/\">"
                + "http://anonscanlations.blogspot.com/</a><br/>"
            + "Licensed (lol) under new BSD<br/>";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ComicHighSite.expand("ZEA*Ve~r@fMtTWTzpwPkVvLj@YvROFTIiDutN@r~zn#COEViGcmfR/?~AppzOFgZpiKKC@ArUPPIwQ*fmDHPptm#emvjtL$Fxph!rRm-fCk-tzgHHEytR/muBcwgOs~$RGcwiJyEvbG!HTBhiXx#epAb?-lVKyVhiUPDgnz!V%:pJ+@*DNM-R@NUgrCHng@$-xVJBY%JMr+F~WLHI/egtm?cABJylOHDCYw%tWtTFZcOKEY$/tyi:D$vEHDhl#ng*gTd!GOrBN#+hZg@mtJOr@vOdQV~u-NgucGd~KqEEQffUTKOzpsf%sSobdsTjVyvme%JFim~FGo$G~rXBLJfHxIXeaY%EqF+wuxr*n%M/h$dwPLyE@PBeG!dJQZLhNH+tZF!AzL~i#LdcPgE@UBWWMXkXLnyrm$pDa!EczazaIl#:CxynQrh/Bddd!+MNX$OWFaiwRJFlQ$NN$ihENkLTIY?m:crJjfjO$Iny!$HO+HS!:~cVaDS-ABsJbACU#vvPndwDyScGlQniCo%lDwP+HzXuVNCz~SPhVpFIr+TfV$dvTuN/KO@MRw-Zj-z@fxsWNyawn*oDwYuApnbkzpBf*JWHlRHKLtVgxFTQwDCGjdbf@g#fyt:YnfJ#j~ImUC#*E~%FKhTAHGxTZ#s@Hr?+MhT/wW~Jhilsh*oIuFjekoL?F%exEWkp?V/tpq%plEwr/Uw*O/qcbgUTe$Zr%Hjdal-LHgjxv/~#-JLXX*NgF*nlTEyKZqokUpYlVKlYxY--p~kRYp/!YJ$asWUaJbCbkJa:$is%VAHW%i+NR@-FKfQGCIUrGkZ%ELN#go-ILBIvjzA-MocINAHcDODkvL!VM@ihVWLyUO#Jy~u@/xDbKl*@Bb!:WCYxHDbYH-bDijDCD@:cEiDqLxqAbU-#/OZP#kFW#*VNJ#vTTHZWuhHL+o+VZhIbHgf-MRzz-pZzsrhgwEPL-AQEyI%EaLymTBoIl/FLB@*$cEESjwJB+uwIy+l+/LBfjSqtq$~X!CJPD!-@!$om:ALrujhcDza:bEADUGZw#bGE$ekvRg*JfwXA-DwjnEKFRTBnM$ylbt/Biy@l#AnADmzHcQxMvUbMPEWz?#aRLYvr%y-q#ErVbXElE-tbQp+lXRdB+k@fGmuQAJ?l+cBAlswe~CbzUXuZfmJvb*ZecOimGC/GMvbp%eAgUQL:ppQce:uaomelQI*p@DlVyQdKJTj:R%YKOF-vOU/dQQ-!q/aseEshxI$uOF~P%KtoqmsHe#p#iUXW#rDi+zXqrKfj~RhwqncKVhp@L!~B%HbATDXQ~QYo+#Q:DO:c?Oq$ma%hZdYJ@aNp:x!ga/!cIjbnA!+Nt%h!vpXZTKk~!*Kye$GIaPTZSbwtTtGjHHRibBAvc:xLv+aX-C@NzGcjCI+eysAlM!QTYu+WR#xyO@Prcgp/TL~-hHbPGJsfu:HxEz$dps/CI~BBSJvXSjzassFzE$l%#:JVGB-h%EeR:SsICc*fDutdOsaBNwh:L#!HwsgtE!oHZ$TjceqEaea$VCz$IAEeeg//GFcqlng@W%s-F@tRVyAkr!dyDX#NAO!YO+kJw*pQvSJvcgq-JcUVSIuqn+UWwwahWYC!zsosZ%*gYt@SL@CEXEZoyufmKUtb#?:$~$zMy?!remp!uCE*s#A-Cp!iFISlLZtf@!RBUidLH!VsnfyyEU*HmT$@totb~-QMTxEYezLqUMsR:LweRq$w+oGptGTE**hwOGlLquZDYCO@vjp@szyG$Fbye:MDGTLZ*R@B:*s$CeJEl@#jYEkCAu%DmaR*wIBsRjcjF#OJvfN#qEqtAlFUS#rhPC-nkWIStzhbg*A!WsnwgPShm:aYFswOZLg$j/fSaVi~!/h%AKPjPYbsAED!@Oj:C+l$!ujhOjgLb/JuuKHcNbLnprqDxE:ViTj!luDvGL*MijhorYOGf-Yi%f%wjAuftg*xtj%AFTkczp$TcJQox?!WUxF-iFLducnTGlUeuLcVWk~hyu$$wyk:#A!!f/ctZhqfTPnfe!roXJnJUOAZZy$+ll/HMrpUkvmgPV+XhCPutXo@ybDRWPwekLucCbJAk~ep!O$UAljVcrkoCqY!HAXoi**Gdx+aeYok@EH:zL%#MW?dTh?%F-*U$kQxy*YO:Nl~xEe%uYm:JykdfrtkghoRFF!I/sDKr/jFhoBJwy$QD@+jqUKpwTDWxppox$IeZI#uZ@!-Yl@JlrrOtWgzHDXhSlVgIPzqZv:SNsyebfG$#hK*sp-mHOKttOkPbw/NQ?+?h:kgmTL+qLWfXd!MINHNYXgp:h?@HJRoaqZ/l?/YYlLoq#mzGxNFnxuIOCQ~kYw%a:dmW!H/$?FRsBJz/+f/ISmOFgNpKR:UqYDnaWcp!%NoAdcPXSSWUvLsAH/tkh-RomtKtJbLCH-imwaHjD+NP#MhZ~rvaFRgrdf+tGAm+NycHzOqhrwRuSfMTUDz/Gi:BNrfod:$GXm$YZwuIhEQB%fruQNDqZjunpW$uzPN$TW?qG#jp*UbBpi$UwY+LLhHuuKJppsLcbyRGjkWlRMyUzKxpKp+Mz+wB:uz#bq/jMYHoZGiAhh+?CY-~+C@/bKG@FH~@o!Mvz?K:B-KvVsLJGat*qYxtNMvBImuUQ@sLQ-og?XFmG@%DGhuArMxqUXDGKP$hy/tNzS*zD/XP+%ufz:/xdEIjX-jKIOlMFWnqyiiYxshDKI-+bbR+$pEGXGcrcUBJHkx?wKlzAKOAbylbNfmnyx*thfbicJyv::CNLEe#CyT%EP-kEZcgJ!AyMt+a~!pY$GdNcVZRkp:xEjIJMwLh?IDuGMTXhY$dnd~kg/TnTSmm-+ayndC/JIEgcifnDQ?NdbKAnAjknDDumonOuXttaVTn#NOmWEAbUi/nOOIw%CdhSK+qg!mIb:zDqpXWz:$DQm~!ULFS:Y$hisvjuJarP-DgrkjMJNYhYXpACUeNqPamDQRfQc:*LgqoHfSI/Q!W?pl~-jmNEEUxkj?Q%MzYzKlkm-OPRF?Ev$hAeCN/Kur:EiAwdPMFfNwxvMiuiJC*~-nlvDl@PBS?GsAgetXApemwKNQ:ng$+:eSPiQOv#L@YVgZ/G~nqmqwgkcvXwZe!?vBQzMNks#GFvOxCVdj$sGhJONR+n~KnQHr+trfbI-#Rw?FmSXkfNyfF/Pv~fcpdKyj?H+$roMFRyjI+~TASGLlubT$XWcxDGaKD~ygHOsDa$WxspmxoO*rLWwQ?M?Vsav@AYOGJq*DRl-+o!DuFbf/~#wdi%/ALjZV*Vb/!MlA%TeOb~iP*hN$!+*HDsqGbYaoSuEgzq*LvYmOvRjL!$L@:IAu$$TQpoMeE%LbBdgAcR+GFzN%TmW/ltERNYoN!RSzpR@J#CojAYLNEBSZcsfztUrHx$nWZJ#sNA*/JnEyN%ygHHJ*phoE/NZeIjzao~R*XA:RMXDLoTJMbZQFXYzVMZojVBJaKJz!iHs@vUaADycSqeeVwJs%?daKPbCq:NyTANVB-ZKua+C!VyvcqedSBHqNXEx::ygPlmJ!:I+fXr~dutF$chS/N-*HCzGNX@HPh$%AfOB%:~SXO%EemTzzPqIfPJzsbEPQ*$CycrS@/HPw?%SPtwbYchSlrALNEFngyFTrzWvVURqlLjZOWrfyMM!BBrh#kFGuhKDr#QI$TJvtQ*pocR$W@arE:qUI%@HoOEkJfLZxjeLkjxhbyjFAACIENocnD$+SyFRWfdmPjBMCI?ZE?PG$afLLIT#$EZRRXDLJflLydZKjLubHGPN-cAKz*jNQqu/OhYfQcuKr%q!SLky!v%EE!#L~DzcGkVOMFm+SUaRriG@#%NI~ulQZEYFlgLB!jTdSnsdAZ+SBBErOGPPgnTjmcZywV-t!YpKMZuO$llytYbc+dpZVtvN~#r*?YmkFEFnbSUgatNrnjjUvElv$V?D-t?a~Mj/C:uSVVf!azHqXjuBs!~Sg:jbgL?+VRa@:WXWAbH$QEL$vRmeNDsXrh$b~CedBVoUktRqPK?AyKyFsurIvx%pQmy$KdJZ/JubfYI*:nDbbfQIBDH!%QDk@#ZmMAzpU!QHO*Ikq%AoOteoPfOb+oTZ-$IBAfeWJ%qbBDtv:Mf!gVeIncOX:qfsj!d!MVhmmz+XRQB/ZEtQAqhdR+hn!jK+ycXreu~Z@dWVn%/T~tTmZFifDNAIpJ#iGJ*tbbydnzmXTCE-qpNCW-ynnOOg/TUSO%nTi!yp~tOcG-vZ+ZfHaAuWDD!?EHU%+MVb:tnaftMbjDufQ:uNigr/ffbXs!JtO?RE-LkQ##z$a?YgsfkQ/glQJ+bXEXXdAv?sE+?gfAQz-Tzd*RlXq-by?liUVTlq-ynbJLvOkQz%l$A-*zJv@t?PJy**Ka*Wz/TNgBW*$jBKbMsZmlKLLAOoVPz!nHET$IlAJ-mFX#ujX#oms?NLJxC*L%VUwEY?ZSyEKKIVrn%zbyASEytDACQSADFAvbt~MtyDjGvVhp/x$-ICZo*ErAgojjBVdbATGe~Mm~OjjV~!FOdBGITdM+SjVauipF#c:ph$zYlFEaxSqS%VJh@pEBNd/Fo#p+FDM+RmYsCaV*ecXnEg!g$eQebpVEB*BkALcWBuiE~:#TrBfbxp+xlj~:MuVO-XbVNW--rDMbcuKQqFfrKqLhUCaHbB?iGziUnz/OAmQX:pfl~!I!vmaGd~JLqp*JbD?RK+yKdMyccIB$cDsixfCz@mOICzEVeB!JFpruX/MKYy#wOUNaX@aBUlS-ASw:OlI?nqZoIMycPBXr-eVQEkC-h+yJeJAK-o#~piG@#jbHrivBP+f-Nw$EA~qahy@P-hfDfGmhpYWOeH/P+HWWpACwfc/jPePASTbswsGdk?zJedD:g@j%*:SiDfMbZ#v-iV*mNwaiFx~fLRXPgV!XFSbw?dMSHufOgGjbdL/?wTFW??lnEnQ/e:c!wtZoH#@JyT*ZpH:W@%CNqzlJAk*?N@Sx@iSrOfV?CKDbEC:iXf:rZl!LMPXFUTGwx?Wkzfdm-lx+tmFRt+GAXxdLFna/sY+sB:rDHT@qCWTO#E:zfmgHzzl?Kh?bdUbYHFcCvEXw!z%nIBa#UpbSmStmLSKQl?%B#/HRag/%uU$hN?ifm$mAqoUJZ#$h%o:x*yLf-Sr!WvB/%o:TrhhAIqsLH$UjFo+@wGHAG*%Qm!naR+HZsJycGI%$hT*vEYCLdvO@r!/Szs$iTn+@VXLrmF!WrBfXVnzJjYLcgS/aL#UEx+iB@LeLMIKEHAk-@+~oTEhHqd%rA@XXKZAh!agbPLU//-UF@WWEvS:@GnXzrbfXKnzhTDACbv$ly!ftR+r:$jzcwkFEHAnDETZo*XMw~gVkL@/JjwyE$gY@Wq#syzsZVZ@vPYWZCSVatfTxgFhS:PiZYr!VV#+eX!e*SXB!qScPWnFuAWhAOVS~zbxaecwEGFepaPHkSvKLo!*qMyuwF:Lo@S!arp#XbFtIFGeO/SmgpBhEaym!%NYcSojDIBeP#Huo*befqsGazo/OVZ~SOLfBQAj:ncLLKeAK%EfcT*-Vn#UbTWu*EXo:/qRVX!yDIVlK/bd#BqEmZzNbRp#KTbD#GSgEmBmaSnP*MDZfCS-Ed!fbQnAN/XofAl:Dwcp?TVlhMLmgVqjd@JpXnTph@bzf@qzoD-NbBptWhdleaS#fYzNb!pXyjon-G@+l@hYrQqwp:RU-*ZPItUFvc*UF$ZDegSQPXAurT/ZhEE:*V*iDp!bdan-t+RzKzzsPxvARb$vW!b#-BZqEFvNbVWu*SlnO-w@PuBMdCSP*SRN-Vq/VPBhD*pE!blF!FnSYneEk-qm*:RYfcSBVnOuLbZOAEE#H$IFYftP!TWk:q!?L*/:cIar!+u?v!GpHb!EEzcwEi*n:WANEF/S#kDA!+vd*dry!@I:guHIbRBvpWgZFqqVa?aZL@Szwc#?aOIlstDUX-k#dM#GN-*aDXAY#qklmiVZ#c*$avEuADXU%%LXfVMSLtbhLEWrhVnDPE%SczAYzQ~kpQzeFqqgflL?NEnCLTDdVWuxEHce#cuLo*EJcGIELhswD*$ML%bdbc!afAcucOkpMbVZ#-*$a?V~CjLiQWzdLR:*PROvEMst%MVZcC*!alVLTG~JhVLkXEsRPlbshi~Jh/EXaxhCtNByBRtN%SNhpFsqczAZtb%DkCEnaOIxK?EufVnPn/DycaJj:wcfajW?G$DPAxva!Y#XEKnehBLZtcItKwYd~idoS:f#DsiS-fQpr+JX%a#lNW*c-IfJ@+JAS$YpR@:GCJAnjg%t?TLZ!Cp:-dQmEWJYwaeprKiWeH!u@K+dwgH#gQr-UX#eUQMDwa-NQVuabJ-AzWipi/DhcGo*DH!Iab?#kOeKC*MOipYz-W:GecH-?PaGTZ-Y-BSzo%#wFWIcwPcyH:$yo%DzcaBGLXrU/E+HEWA#jgScHnAiP/NPvqC#!$s!ELcYwMsmNMTecBOOOpRYB*+Av*EUDEoMTZctLbfwwc!jki*gOdFL!VnYaqxdlCHsEYmJuQznO@StZ/@GkLMou-zdM@azG!eq~mVUEMAlk+Cd$cuxEjmekcxHmi-nDsUMkpnd$cc?%R/rca*iw#XLAW#?G$Df+xUGTZGuu:cvhVE?FsMXklgdajZhhFaJojmKmZvpX*gwf*bYCeopgyBoJWedfsa?AEmvnzB%xzp*e!b-Zpr%neSTLjgwzerB/aS@V!H:LjZ#ohlCzVceY/NgfCCxJGNvhtCxV$BeXGpfeKgwqOOMtDPzf+idziJrfjMOOLAlLb~KKSmzAhrvW+UKO~jZ*saY#oVq@~ctLif#k+dNa-VgAhcH~DPymKj~CJz?gy!*iKYZadh#nRkGdlvm#G#B:OupjjixaP#e:SkngizISojs~LZnxTCW!BkJgoxV~A+I#F+es*#o#Cvpqrvtb/+mp!urRa*psHzIEKZASBr-/qqVVAlkceroW@E:pcZ!#HAna!LHS@ZC+c$Glt-yJ?!DWM#APLRNAhzp!VvyvFvHC*mVEL!daVpoo@XZ#VYjP:StbjWmT$DvNoRp/htfE*qLWEYm#jTCzAD!WqV~hFEZU-qQyAALp#Kuhbpz~SS+ZJch+aDOHBDtkEQi$o/JY:oZo~oX#WSHH:t/#MbAxVDp~S*+WhvoFSVflMyUgsiaTHA?DCVyh-ykzS%tlvwXacbAy+pZt%gQyp~fF-bwLMWlzFggFXswLbDDRQ+AA!Qxnr/?bgodcFXNOxq/VO$pzOPDrxDw:SJ:vuoYhSgfr:fNw@X#Sl~Hp:gpbQvX:$lKDUx*hSXpjWSDdjQ%SBvzaSNZ*$mDUGa+SgGdD!Gep@m?J!Y+qwM~FOg#XGvkiVDWq?iupSl#R?hPeUy!fzJo!/raMzaLAv*gBTE%#PkPO%oL+NYOzsdyke@je/seZw?AaVLZ#@L@Y~:fZdCTzaowWPHY/m/~Nc!lHsX?FfWPp*ifbtRjA-#-*!zDOuY@D/$@VUtZELGEMkc-e!%@uVFiXPpDLyEH%bT/J/BuDufgDo:CsAgdzWJLpKc+btGPGSEvxE%dH*UFwxr#fna!PBadSD~%$KWe*wRFCu?#mmOKX!iKxZm+m$E$ZAv%agYW*qjGguZppDiHBrIG%sRDXuPowlynmSD:T:D@ncr:#QkM+:?QLkUROQzbwY!ACYDuJp@rl*XyJuK*l:?REZABr*PWj?$ZHm!UnWdkGwAdnGzR*xOPBIFAsTgGzPpWbXEJIcYV~zW", 39867821);
        //ComicHighSite.expand("ZEA*Ve~r@fMtTWTzpwPkVvLj@YvROFTIiDutN@r~zn#COEViGcmfR/?~AppzOFgZpiKKC@ArUPPIwQ*fmDHPptm#emvjtL$Fxph!rRm-fCk-tzgHHEytR/muBcwgOs~$RGcwiJyEvbG!HTBhiXx#epAb?-lVKyVhiUPDgnz!V%:pJ+@*DNM-R@NUgrCHng@$-xVJBY%JMr+F~WLHI/egtm?cABJylOHDCYw%tWtTFZcOKEY$/tyi:D$vEHDhl#ng*gTd!GOrBN#+hZg@mtJOr@vOdQV~u-NgucGd~KqEEQffUTKOzpsf%sSobdsTjVyvme%JFim~FGo$G~rXBLJfHxIXeaY%EqF+wuxr*n%M/h$dwPLyE@PBeG!dJQZLhNH+tZF!AzL~i#LdcPgE@UBWWMXkXLnyrm$pDa!EczazaIl#:CxynQrh/Bddd!+MNX$OWFaiwRJFlQ$NN$ihENkLTIY?m:crJjfjO$Iny!$HO+HS!:~cVaDS-ABsJbACU#vvPndwDyScGlQniCo%lDwP+HzXuVNCz~SPhVpFIr+TfV$dvTuN/KO@MRw-Zj-z@fxsWNyawn*oDwYuApnbkzpBf*JWHlRHKLtVgxFTQwDCGjdbf@g#fyt:YnfJ#j~ImUC#*E~%FKhTAHGxTZ#s@Hr?+MhT/wW~Jhilsh*oIuFjekoL?F%exEWkp?V/tpq%plEwr/Uw*O/qcbgUTe$Zr%Hjdal-LHgjxv/~#-JLXX*NgF*nlTEyKZqokUpYlVKlYxY--p~kRYp/!YJ$asWUaJbCbkJa:$is%VAHW%i+NR@-FKfQGCIUrGkZ%ELN#go-ILBIvjzA-MocINAHcDODkvL!VM@ihVWLyUO#Jy~u@/xDbKl*@Bb!:WCYxHDbYH-bDijDCD@:cEiDqLxqAbU-#/OZP#kFW#*VNJ#vTTHZWuhHL+o+VZhIbHgf-MRzz-pZzsrhgwEPL-AQEyI%EaLymTBoIl/FLB@*$cEESjwJB+uwIy+l+/LBfjSqtq$~X!CJPD!-@!$om:ALrujhcDza:bEADUGZw#bGE$ekvRg*JfwXA-DwjnEKFRTBnM$ylbt/Biy@l#AnADmzHcQxMvUbMPEWz?#aRLYvr%y-q#ErVbXElE-tbQp+lXRdB+k@fGmuQAJ?l+cBAlswe~CbzUXuZfmJvb*ZecOimGC/GMvbp%eAgUQL:ppQce:uaomelQI*p@DlVyQdKJTj:R%YKOF-vOU/dQQ-!q/aseEshxI$uOF~P%KtoqmsHe#p#iUXW#rDi+zXqrKfj~RhwqncKVhp@L!~B%HbATDXQ~QYo+#Q:DO:c?Oq$ma%hZdYJ@aNp:x!ga/!cIjbnA!+Nt%h!vpXZTKk~!*Kye$GIaPTZSbwtTtGjHHRibBAvc:xLv+aX-C@NzGcjCI+eysAlM!QTYu+WR#xyO@Prcgp/TL~-hHbPGJsfu:HxEz$dps/CI~BBSJvXSjzassFzE$l%#:JVGB-h%EeR:SsICc*fDutdOsaBNwh:L#!HwsgtE!oHZ$TjceqEaea$VCz$IAEeeg//GFcqlng@W%s-F@tRVyAkr!dyDX#NAO!YO+kJw*pQvSJvcgq-JcUVSIuqn+UWwwahWYC!zsosZ%*gYt@SL@CEXEZoyufmKUtb#?:$~$zMy?!remp!uCE*s#A-Cp!iFISlLZtf@!RBUidLH!VsnfyyEU*HmT$@totb~-QMTxEYezLqUMsR:LweRq$w+oGptGTE**hwOGlLquZDYCO@vjp@szyG$Fbye:MDGTLZ*R@B:*s$CeJEl@#jYEkCAu%DmaR*wIBsRjcjF#OJvfN#qEqtAlFUS#rhPC-nkWIStzhbg*A!WsnwgPShm:aYFswOZLg$j/fSaVi~!/h%AKPjPYbsAED!@Oj:C+l$!ujhOjgLb/JuuKHcNbLnprqDxE:ViTj!luDvGL*MijhorYOGf-Yi%f%wjAuftg*xtj%AFTkczp$TcJQox?!WUxF-iFLducnTGlUeuLcVWk~hyu$$wyk:#A!!f/ctZhqfTPnfe!roXJnJUOAZZy$+ll/HMrpUkvmgPV+XhCPutXo@ybDRWPwekLucCbJAk~ep!O$UAljVcrkoCqY!HAXoi**Gdx+aeYok@EH:zL%#MW?dTh?%F-*U$kQxy*YO:Nl~xEe%uYm:JykdfrtkghoRFF!I/sDKr/jFhoBJwy$QD@+jqUKpwTDWxppox$IeZI#uZ@!-Yl@JlrrOtWgzHDXhSlVgIPzqZv:SNsyebfG$#hK*sp-mHOKttOkPbw/NQ?+?h:kgmTL+qLWfXd!MINHNYXgp:h?@HJRoaqZ/l?/YYlLoq#mzGxNFnxuIOCQ~kYw%a:dmW!H/$?FRsBJz/+f/ISmOFgNpKR:UqYDnaWcp!%NoAdcPXSSWUvLsAH/tkh-RomtKtJbLCH-imwaHjD+NP#MhZ~rvaFRgrdf+tGAm+NycHzOqhrwRuSfMTUDz/Gi:BNrfod:$GXm$YZwuIhEQB%fruQNDqZjunpW$uzPN$TW?qG#jp*UbBpi$UwY+LLhHuuKJppsLcbyRGjkWlRMyUzKxpKp+Mz+wB:uz#bq/jMYHoZGiAhh+?CY-~+C@/bKG@FH~@o!Mvz?K:B-KvVsLJGat*qYxtNMvBImuUQ@sLQ-og?XFmG@%DGhuArMxqUXDGKP$hy/tNzS*zD/XP+%ufz:/xdEIjX-jKIOlMFWnqyiiYxshDKI-+bbR+$pEGXGcrcUBJHkx?wKlzAKOAbylbNfmnyx*thfbicJyv::CNLEe#CyT%EP-kEZcgJ!AyMt+a~!pY$GdNcVZRkp:xEjIJMwLh?IDuGMTXhY$dnd~kg/TnTSmm-+ayndC/JIEgcifnDQ?NdbKAnAjknDDumonOuXttaVTn#NOmWEAbUi/nOOIw%CdhSK+qg!mIb:zDqpXWz:$DQm~!ULFS:Y$hisvjuJarP-DgrkjMJNYhYXpACUeNqPamDQRfQc:*LgqoHfSI/Q!W?pl~-jmNEEUxkj?Q%MzYzKlkm-OPRF?Ev$hAeCN/Kur:EiAwdPMFfNwxvMiuiJC*~-nlvDl@PBS?GsAgetXApemwKNQ:ng$+:eSPiQOv#L@YVgZ/G~nqmqwgkcvXwZe!?vBQzMNks#GFvOxCVdj$sGhJONR+n~KnQHr+trfbI-#Rw?FmSXkfNyfF/Pv~fcpdKyj?H+$roMFRyjI+~TASGLlubT$XWcxDGaKD~ygHOsDa$WxspmxoO*rLWwQ?M?Vsav@AYOGJq*DRl-+o!DuFbf/~#wdi%/ALjZV*Vb/!MlA%TeOb~iP*hN$!+*HDsqGbYaoSuEgzq*LvYmOvRjL!$L@:IAu$$TQpoMeE%LbBdgAcR+GFzN%TmW/ltERNYoN!RSzpR@J#CojAYLNEBSZcsfztUrHx$nWZJ#sNA*/JnEyN%ygHHJ*phoE/NZeIjzao~R*XA:RMXDLoTJMbZQFXYzVMZojVBJaKJz!iHs@vUaADycSqeeVwJs%?daKPbCq:NyTANVB-ZKua+C!VyvcqedSBHqNXEx::ygPlmJ!:I+fXr~dutF$chS/N-*HCzGNX@HPh$%AfOB%:~SXO%EemTzzPqIfPJzsbEPQ*$CycrS@/HPw?%SPtwbYchSlrALNEFngyFTrzWvVURqlLjZOWrfyMM!BBrh#kFGuhKDr#QI$TJvtQ*pocR$W@arE:qUI%@HoOEkJfLZxjeLkjxhbyjFAACIENocnD$+SyFRWfdmPjBMCI?ZE?PG$afLLIT#$EZRRXDLJflLydZKjLubHGPN-cAKz*jNQqu/OhYfQcuKr%q!SLky!v%EE!#L~DzcGkVOMFm+SUaRriG@#%NI~ulQZEYFlgLB!jTdSnsdAZ+SBBErOGPPgnTjmcZywV-t!YpKMZuO$llytYbc+dpZVtvN~#r*?YmkFEFnbSUgatNrnjjUvElv$V?D-t?a~Mj/C:uSVVf!azHqXjuBs!~Sg:jbgL?+VRa@:WXWAbH$QEL$vRmeNDsXrh$b~CedBVoUktRqPK?AyKyFsurIvx%pQmy$KdJZ/JubfYI*:nDbbfQIBDH!%QDk@#ZmMAzpU!QHO*Ikq%AoOteoPfOb+oTZ-$IBAfeWJ%qbBDtv:Mf!gVeIncOX:qfsj!d!MVhmmz+XRQB/ZEtQAqhdR+hn!jK+ycXreu~Z@dWVn%/T~tTmZFifDNAIpJ#iGJ*tbbydnzmXTCE-qpNCW-ynnOOg/TUSO%nTi!yp~tOcG-vZ+ZfHaAuWDD!?EHU%+MVb:tnaftMbjDufQ:uNigr/ffbXs!JtO?RE-LkQ##z$a?YgsfkQ/glQJ+bXEXXdAv?sE+?gfAQz-Tzd*RlXq-by?liUVTlq-ynbJLvOkQz%l$A-*zJv@t?PJy**Ka*Wz/TNgBW*$jBKbMsZmlKLLAOoVPz!nHET$IlAJ-mFX#ujX#oms?NLJxC*L%VUwEY?ZSyEKKIVrn%zbyASEytDACQSADFAvbt~MtyDjGvVhp/x$-ICZo*ErAgojjBVdbATGe~Mm~OjjV~!FOdBGITdM+SjVauipF#c:ph$zYlFEaxSqS%VJh@pEBNd/Fo#p+FDM+RmYsCaV*ecXnEg!g$eQebpVEB*BkALcWBuiE~:#TrBfbxp+xlj~:MuVO-XbVNW--rDMbcuKQqFfrKqLhUCaHbB?iGziUnz/OAmQX:pfl~!I!vmaGd~JLqp*JbD?RK+yKdMyccIB$cDsixfCz@mOICzEVeB!JFpruX/MKYy#wOUNaX@aBUlS-ASw:OlI?nqZoIMycPBXr-eVQEkC-h+yJeJAK-o#~piG@#jbHrivBP+f-Nw$EA~qahy@P-hfDfGmhpYWOeH/P+HWWpACwfc/jPePASTbswsGdk?zJedD:g@j%*:SiDfMbZ#v-iV*mNwaiFx~fLRXPgV!XFSbw?dMSHufOgGjbdL/?wTFW??lnEnQ/e:c!wtZoH#@JyT*ZpH:W@%CNqzlJAk*?N@Sx@iSrOfV?CKDbEC:iXf:rZl!LMPXFUTGwx?Wkzfdm-lx+tmFRt+GAXxdLFna/sY+sB:rDHT@qCWTO#E:zfmgHzzl?Kh?bdUbYHFcCvEXw!z%nIBa#UpbSmStmLSKQl?%B#/HRag/%uU$hN?ifm$mAqoUJZ#$h%o:x*yLf-Sr!WvB/%o:TrhhAIqsLH$UjFo+@wGHAG*%Qm!naR+HZsJycGI%$hT*vEYCLdvO@r!/Szs$iTn+@VXLrmF!WrBfXVnzJjYLcgS/aL#UEx+iB@LeLMIKEHAk-@+~oTEhHqd%rA@XXKZAh!agbPLU//-UF@WWEvS:@GnXzrbfXKnzhTDACbv$ly!ftR+r:$jzcwkFEHAnDETZo*XMw~gVkL@/JjwyE$gY@Wq#syzsZVZ@vPYWZCSVatfTxgFhS:PiZYr!VV#+eX!e*SXB!qScPWnFuAWhAOVS~zbxaecwEGFepaPHkSvKLo!*qMyuwF:Lo@S!arp#XbFtIFGeO/SmgpBhEaym!%NYcSojDIBeP#Huo*befqsGazo/OVZ~SOLfBQAj:ncLLKeAK%EfcT*-Vn#UbTWu*EXo:/qRVX!yDIVlK/bd#BqEmZzNbRp#KTbD#GSgEmBmaSnP*MDZfCS-Ed!fbQnAN/XofAl:Dwcp?TVlhMLmgVqjd@JpXnTph@bzf@qzoD-NbBptWhdleaS#fYzNb!pXyjon-G@+l@hYrQqwp:RU-*ZPItUFvc*UF$ZDegSQPXAurT/ZhEE:*V*iDp!bdan-t+RzKzzsPxvARb$vW!b#-BZqEFvNbVWu*SlnO-w@PuBMdCSP*SRN-Vq/VPBhD*pE!blF!FnSYneEk-qm*:RYfcSBVnOuLbZOAEE#H$IFYftP!TWk:q!?L*/:cIar!+u?:OAlE@/ijdBZ!EZppa!tE!JQ#kDAASxwdGAtziq!DfAAj$Sfyb#D!Hfxa?aZL@SzwcrttiHsL-FZAxWl%xGpHPjja##JD+G?%b#kL-qjaucAc-$k%QcbN-ogVlEPDg~eUVDmGB%-fzltD+stMOrkVRIifpLwNEnCLTDdV+W%apLu!!Wrv$Dm#QIeXzAXlK$?$afPCVmRaNE~ckXUdKaYA:*qBNVpDwPX?a!tXfFEaIXmcQxYh/EeAV:WOlCXDs$I/WTrCV%iBpCXDRZtmOPtjYPiAh!n:OklZBikY/?%LtNPHQ%v*MVZcSbq!lPof%:ehKAPh/*/EHzPCR+HSOXlDbo*am!pDF$J$%DCd/wgcfjUEgiNMiOINxxVlkQZBGxNxVEUr*hC-IjXE!KdiOHtggFgt#XDL*$eZg@IrB*W-LpUDjhtw:LdRskEfplHH+JPNqUJO@k%EY#Pe@+d~Ot?gj~sDfj#Ej+~hTLZ!cqVEfRHEq/Bobc#yEIeXIY!w+%-VIq?Nb$+@ezwXjGvUVDUFc*Qtn#UXj+YhK%?cRkBUZAnSCxpk/bUg*T-b?ykER$l/SLwXCq!akyezKEUG:itYExqbJAvL@ZD%qLkNb*G@wY~TNIlGciIa+qTSlFAeMuzKKvtZxk*wy!kSCAnO-uHJ@eg@InIng!kecx~AF@CfdV~u!/ZKhvd+@T$BpXrf-cNtCuHujU:!hHIDTZp--zpa@Jq#?VpDOcIkMGN+TxB+Nnek-+ySa~hOezi~vZNajZhhFaJojmKmZvpX*g~o@dJCeopgyBoJWwUC-JlAEmvnfHSELO!XpcKMB#/:+#nx+-%vfBefbSLYEeuXH$O~zq+VyGQ/K#rwJi#oW%gVWSuVpzHbbCDhbJY#SOMtUPHf:i~yO?NhWSc!lVpDF$fMzafKb*kn:iTrDdFDEbY#-qwS?ylEx$p@iHDSTJBSHpUaVZn-UFmaaF%PivBjsnC@TKJmAP-x#yJmiTX%MGTLxe:tUjwEB$pgiB?uCqjDthTFsCLH%xzWoCFuZY@@VCAOs#hcCG@Au!pXprr#EbucGZ$kAlkEpfHosEyAO*Fla?UaVpAwKc!JW$*aWFnr!xHh%aeuc**ngZnRG*tW/bYCeecQeBpWNKbmRRJL-WsDPm?XiwHK$ae#-S/PJzXa-Vrot%JfcvKxLArOtLiBjYuAincWoemsgiAdNWniOIBaJ@IaQPohOpcT:IiyZitb+DeSG+UkLUFLXxH+:D~VWYweSvPIt!fbH:dQX$U~+ISW~cYF*TflMyUsEYiHmtpDSdAhFDts%dSpJbxr:bHK+pH#jgRjksJp:b~QqWN:jgcbIsuWiXovi+#w/Q/atxNZ@lP*:RphgvbYd!fbN#e?v$ba+vRLJAAv%xvDT?I:@XQqlmrv-bdLQfNw!gC$A#y+NjdRg+ddjbO$m:qmMndEjbeKB+K*k:H*j+Lm/rZKKEWqZHJ*:rDrSoD:REvSl~YobrDRGqLeU!lPZ%piKKC@!:UPt/~S$UmB@zaM$oAs#ZRDTLd+FCjKymfXSfHy$ZpwPWDlJE@VCLr$z$pDoODhe~BUfQ@LxpwpwjSadyTV#oiDCRppyBr*kD:f%+Ra%fmBBRkRsndkdhBGVfTFwKiRJuzwgT!WMv:LN?eylWeidATySRup%@IXV/LP$sWa~sHBhcPTaBWtJI$XyOp~RKCBvbeWt**KcbagFXEh~A@C#yEm@u$HFpsldZvtU:gNVaM*g@MysF?H:URycPd%rFuJi$M!:NaxaRKwAEjxBlVEmWXlNi@u#s!+Y$E!fAzR$O-rmS*X$?VayuBJK:zPHIEnL!~Zdzw-/zlFkYGhF*NoaOHshnO%yOpylwZWO@MQ/CUXVzeo", 39867821);
        for(int i = 1; i < 4; i++)
            System.out.println("str: " + ComicHighSite.getIpntStr(167, i, 0, 0, 0));

        /*
        try
        {
            DownloadInfoServer.loadAllInfo();
        }
        catch(Exception e)
        {
            DownloaderUtils.errorGUI("Couldn't load info on series or magazines", e, true);
        }

        DownloadInfoServer.SITES.put(SundaySite.SITE.getName(), SundaySite.SITE);
        DownloadInfoServer.SITES.put(GanGanOnlineSite.SITE.getName(), GanGanOnlineSite.SITE);
        DownloadInfoServer.SITES.put(YahooComicSite.SITE.getName(), YahooComicSite.SITE);

        PreferencesManager.initializePrefs();

        if(args.length >= 2 && args[0].equals("--server"))
        {
            server(args[1]);
        }
        else if(args.length == 0)
        {
            client();
        }
        // */
    }

    private static void server(String file)
    {
        DownloaderUtils.debug("running server");

        TreeMap<String, Magazine> magazines =
                new TreeMap<String, Magazine>();

        for(Map.Entry<String, Site> entry : DownloadInfoServer.SITES.entrySet())
        {
            TreeMap<String, Magazine> siteMagazines = null;
            try
            {
                siteMagazines = entry.getValue().getMagazines();
            }
            catch(IOException ioe)
            {
                DownloaderUtils.error("Could not get data on magazine \""
                                    + entry.getKey() + "\"", ioe, false);
            }
            magazines.putAll(siteMagazines);
        }

        SaveData data = new SaveData();
        data.setMagazines(magazines);
        data.resetDate();
        try
        {
            data.dumpYAML(file);
        }
        catch(IOException ioe)
        {
            DownloaderUtils.error("Could not save data", ioe, true);
        }
    }

    private static void client()
    {
        DownloaderUtils.debug("running client");

        SaveData data = null;

        try
        {
            data = DownloaderUtils.readYAML("data/manga_download_info.yml");

            final DownloaderWindow window = new DownloaderWindow(data);

            if(PreferencesManager.PREFS.getBoolean(PreferencesManager.KEY_SERVERCHECK, true))
                DownloaderUtils.refreshFromServer(window);

            window.setVisible(true);
        }
        catch(Exception e)
        {
            DownloaderUtils.errorGUI("Could not retreive data from file", e, true);
        }
    }
}
