import java.io.*;

/*
    cd "/Users/infidelis/IdeaProjects/Interpolirovanie/"
        plot "polinomOut3.txt" w l, "points.txt" w p, "functionOut.txt" w l, "difference.txt" w l, "zero.txt" w l

 */


public class Main {
    public static void main(String[] args)
                                throws IOException{

        File file = new File("out.txt");
        FileWriter writer = new FileWriter(file);

        File pointFile = new File("points.txt");
        FileWriter pointer = new FileWriter(pointFile);

        File funcFile = new File("functionOut.txt");
        FileWriter writerFunc = new FileWriter(funcFile);

        File difference = new File("difference.txt");
        FileWriter diff = new FileWriter(difference);


        double x = 0.5;
        double h = 0.1;
        int degree = 3;

        double X[] = FillArray(x, h, degree);

        double lineSegment = 2;


        System.out.println("Polinom: " + Polinom(X, x, 3 ));
        System.out.println("Function: " + Func(x));


        double[] a = new double[degree+1];
        for (int i = 0; i <= degree; i++) {                           // Высчитывание Разделенной разности
            a[i] = DividedDifference(X, i, 0);
        }



        for (double x0 = -lineSegment; x0 < lineSegment; x0 += 0.1 ) { // Вывод значения функции в файл functionOut.txt
            writerFunc.write(x0 + " " + Func(x0) + "\n");
            writerFunc.flush();
        }

        for (double point: X) {                                        // Вывод значения функции в файл
            pointer.write(point + " " + Func(point) + "\n");
            pointer.flush();
        }

        for (double point = x - 6*h ; point <= x + 6*h; point += 0.01   ) { // Вывод значения функции в файл
            double tmp = Math.abs(Func(point) - WidePolinom(X,point,degree, a));
            diff.write(point + " " + 5000*tmp + "\n");
            diff.flush();
        }

        for (int i = 0; i <= degree; i++) {                             // Вывод значения функции в файл
            PrintPoints(X, "polinomOut"+ i + ".txt", i, lineSegment, 0.1, 0);
            writer.write("\n\n\nПолином " + i + " степени:\n");
            PrintData(x, h, writer, i);
        }
    }


    static double AnotherH(double x, double h, int degree) {
        double[] X = FillArray(x, h, degree);
        return  Math.abs(Func(x) - Polinom( X, x, degree));
    }



    static double[] FillArray(double x, double h, int degree) {
        double X[] = new double[degree + 1];

        for (int i = 0; i <= degree; i++) {
            X[i] = x + (2*i - 3)*h;     // 0.5 - 6.103515625*10^-6

           // System.out.println(i + " ");
        }

        return X;
    }



    static double DividedDifference(double[] X, int i, int k) { // k < 2, i = length - 1

            return i == 0 ?
                    Func(X[k]) :
                    (DividedDifference( X, i-1, k+1) - DividedDifference( X, i-1, k) ) / ( X[k + i] - X[k]);
    }


    static double Func (double x) {

        return Math.sin(x);
    }



    static double Polinom(double X[] , double x, int degree) {
        double[] a = new double[degree+1];
        for (int i = 0; i <= degree; i++) {
            a[i] = DividedDifference(X, i, 0);
            //a[i] = Lagrange(X, i, degree);
        }

        return WidePolinom(X, x, degree, a);
    }



    static double WidePolinom(double X[] , double x, int degree, double[] a) {
        double tmp = 0;

        for (int i = 0; i <= degree; i++) {

            double TMP = a[i];
            for (int j = 1; j <= i; j++) {
                TMP *= (x - X[j-1]);
            }
            tmp += TMP;
        }

        return tmp ;
    }


    static void PrintData(double x, double h, FileWriter writer, int degree) throws IOException{
        double pow = 0.5;
        String space = "    ";

        double prevPrevTMP = 0;
        double prevTMP = 0;

        double m = 0;
        double n = 0;


        for (int i = 1; i < 200; i++) {

            int switcher = 0;
            if(i >= 2 ) switcher = 1;
            if ( i >= 3 ) switcher = 2;

            pow *= 2;
            double step = h / pow;
            double tmp = AnotherH(x, step, degree);

            writer.write(i + space + step + space + tmp + space);

            switch (switcher) {
                case 0:
                    writer.write("\n");
                    break;
                case 1:
                    m = Math.log10(Math.abs(prevTMP/tmp)) / Math.log10(2);
                    writer.write( m + "\n");
                    break;
                case 2:
                    m = Math.log10(Math.abs(prevTMP/tmp)) / Math.log10(2);
                    n = Math.log10(Math.abs(prevPrevTMP - prevTMP) / Math.abs(prevTMP - tmp)) / Math.log10(2);
                    writer.write( m + space + n + "\n");
                    break;
            }

            prevPrevTMP = prevTMP;
            prevTMP = tmp;

            writer.flush();

            if(prevTMP == 0 | Double.isNaN(prevTMP)) {
                h = step;
                break;
            }
        }

        double[] X = FillArray(x, h, degree);

        String str = "{";
        for (int i = 1; i <= degree + 1; i++) {
            str += "y" +i + ", ";
        }
        str += "}: ";

        writer.write(  "\n\n" +"h: " + h + "\n");
        writer.write(str);

        for (int i = 0; i < degree; i++) {
            writer.write(Func(X[i]) + " ");
        }

        writer.write("\nЗначение полинома в точке х0: " + Polinom(X, x, degree) + "\n");
        writer.write("Значение функции в точке x0:  " + Func(x) + "\n");
        writer.flush();
    }


    static  void PrintPoints(double X[], String polinomFileName, int degree, double lineSegment, double smallStep, double difference)
            throws IOException {

        File polinomFile = new File(polinomFileName);
        FileWriter writerPol = new FileWriter(polinomFile);

        //System.out.println("PrintPoints");
        double[] a = new double[degree+1];

        for (int i = 0; i <= degree; i++) {
            a[i] = DividedDifference(X, i, 0);
            //writerPol.write(a[i] +" c ");
            //System.out.println("Разделенная разность: " + a[i] + " степень полинома: " + i);
        }

        for (double x = -lineSegment; x < lineSegment; x += smallStep ) {
            double tmp = WidePolinom(X, x, degree, a) + difference;
            writerPol.write(x + " " + tmp + "\n");
            writerPol.flush();
        }
    }
}