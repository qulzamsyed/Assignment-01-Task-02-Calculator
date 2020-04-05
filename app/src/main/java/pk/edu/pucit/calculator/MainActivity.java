package pk.edu.pucit.calculator;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    TextView equation;
    TextView result;

    private boolean numFlag=false;
    private boolean dotFlag=true;
    private boolean isExpressionCorrect=false;
    private boolean operatorFlag=false;
    private boolean negativeFlag=true;
    private boolean percentFlag=false;

   private int[] numbers={R.id.zero,R.id.doubleZero,R.id.one,R.id.two,R.id.three,R.id.four,R.id.five,R.id.six,R.id.seven,R.id.eight,R.id.nine};
   private int[] operators={R.id.plus,R.id.minus,R.id.divide,R.id.percent,R.id.multiply};

    public void clearClick(View view){
        equation.setText("");
        result.setText("0");
        numFlag=false;
        dotFlag=true;
        negativeFlag=true;
        operatorFlag=false;
        percentFlag=false;
        isExpressionCorrect=false;
    }

    public void delClick(View view){

        String str=equation.getText().toString();
        if(str!=null && str.length()>0)
        {
            if(str.length()==1)
            {
                equation.setText("");
                numFlag=false;
                dotFlag=true;
                isExpressionCorrect=false;
            }
            else{
                char previous=str.charAt(str.length()-1);
                str=str.substring(0,str.length()-1);
                equation.setText(str);
                int len=str.length();
                char ch=str.charAt(len-1);
                if(ch>='0' && ch<='9')
                {
                    if(previous=='.' || previous=='%')
                    {
                        dotFlag=true;
                    }
                    numFlag=true;
                    isExpressionCorrect=true;
                }
                else if(ch=='.')
                {
                    dotFlag=false;
                    isExpressionCorrect=false;
                }
                else if(ch=='+' || ch=='-' || ch=='*' || ch=='/'){
                    numFlag=false;
                    dotFlag=true;
                    isExpressionCorrect=false;
                }
                else if(ch=='%')
                {
                    percentFlag=true;
                    numFlag=true;
                    dotFlag=false;
                    isExpressionCorrect=true;
                }
                else {
                    numFlag=false;
                    dotFlag=true;
                    isExpressionCorrect=false;
                }
            }

        }

    }

    private String percentHandler()
    {
        String eq=equation.getText().toString();
        String num="";
        int count=0;
        Double preResult=0.0;
        boolean firstPercentFlag=false;
        String newEq="";
        Stack<Double> numbers=new Stack<Double>();
        Stack<Character> operators=new Stack<Character>();
        int length=eq.length();
        for (int i=0; i<length;i++) {
            char ch = eq.charAt(i);
            if (ch >= '0' && ch <= '9') {
                num = num + ch;
                if (i == length - 1) {
                    double temp = Double.parseDouble(num);
                    numbers.push(temp);
                }
            } else {
                if(num!="")
                {
                    double temp = Double.parseDouble(num);
                    numbers.push(temp);
                    if(preResult!=0.0 && ch!='%')
                    {
                        preResult=preResult+temp;
                    }
                }
                if(ch=='%')
                {
                    count++;
                    double no=numbers.pop();
                    if(!operators.empty())
                    {
                        char op=operators.peek();
                        if(op=='*' || op=='/' )
                        {
                            no=no/100;
                        }
                        else{

                           if(count<=1)
                           {
                               double pre=numbers.peek();
                               no=pre/100*no;
                               preResult=no+pre;
                           }
                           else{
                               if(firstPercentFlag)
                               {
                                   double num1=numbers.pop();
                                   if(numbers.empty())
                                   {
                                       preResult=num1;
                                       numbers.push(num1);
                                       firstPercentFlag=false;

                                   }
                                    else{
                                       double num2=numbers.pop();
                                       if(op=='+')
                                       {
                                           preResult=num1+num2;
                                       }
                                       else{
                                           preResult=num1-num2;
                                       }
                                       numbers.push(num2);
                                       numbers.push(num1);
                                       firstPercentFlag=false;
                                   }
                               }
                               no=preResult/100*no;
                               preResult=preResult+no;
                           }
                        }
                    }
                    else{
                        if(count==1)
                        {
                            firstPercentFlag=true;
                        }
                        no=no/100;
                    }
                    numbers.push(no);
                }
                else{
                    operators.push(ch);
                }
                num = "";
            }
        }
        while(!numbers.empty()) {
            if(numbers.size()>1 && operators.empty())
            {
                while(!numbers.empty())
                {
                    Double temp1 = numbers.pop();
                    String temp2 = Double.toString(temp1);
                    newEq = newEq + temp2;
                    newEq=newEq+"*";
                }
                newEq=newEq.substring(0,newEq.length()-1);
            }
            else{
                Double temp1 = numbers.pop();
                String temp2 = Double.toString(temp1);
                newEq = newEq + temp2;
                if (!operators.empty()) {
                    newEq = newEq + operators.pop();
                }
            }

        }
        Stack<String> no=new Stack<>();
        Stack<String> op=new Stack<>();
        String temp=newEq;
        String str="";
        for (int i = 0; i < temp.length(); i++){
            char c = temp.charAt(i);
            if(c>='0' && c<='9')
            {
                str=str+c;
            }
            else if(c=='.')
            {
                str=str+c;
            }
            else{
                no.push(str);
                str="";
                op.push(Character.toString(c));
            }
        }
        no.push(str);
        String temp1="";
        while(!no.empty())
        {
            String snum=no.pop();
            String sop="";
            if(!op.empty())
            {
                sop=op.pop();
            }

            temp1=temp1+snum+sop;
        }
        Log.i("euation", "percentHandler: "+temp1);
        return temp1;
    }


    public void equalClick(View view){
        try{
            String eq;
            Expression expression;
            double res;
           if(isExpressionCorrect && !percentFlag)
           {
                eq=equation.getText().toString();
               expression=new ExpressionBuilder(eq).build();
              res=expression.evaluate();
               result.setText(Double.toString(res));
           }
           else if(isExpressionCorrect){
                eq=percentHandler();
               expression=new ExpressionBuilder(eq).build();
               res=expression.evaluate();
               result.setText(Double.toString(res));
           }

        }
        catch (ArithmeticException error)
        {
            result.setText("Error");
        }
    }

   private void assigningNumbersListener(){
       View.OnClickListener listener=new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               TextView tv=(TextView)v;
               String number=tv.getText().toString();
               equation.append(number);
               numFlag=true;
               isExpressionCorrect=true;
           }
       };
       for(int i=0;i<numbers.length;i++)
       {
           findViewById(numbers[i]).setOnClickListener(listener);
       }
   }

    private void assigningOperatorsListener(){
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv=(TextView)v;
                String operator=tv.getText().toString();
                if(numFlag)
                {
                    equation.append(operator);
                    if(operator.equals("%"))
                    {
                        percentFlag=true;
                        numFlag=true;
                        dotFlag=false;
                        isExpressionCorrect=true;
                        operatorFlag=false;
                    }
                    else{
                        numFlag=false;
                        dotFlag=true;
                        isExpressionCorrect=false;
                        operatorFlag=true;
                    }
                }
                else if(!numFlag && operatorFlag)
                {
                    if(!operator.equals("%"))
                    {
                        delClick(v);
                        equation.append(operator);
                        numFlag=false;
                        dotFlag=true;
                        isExpressionCorrect=false;
                        operatorFlag=true;
                    }
                }
                else if(negativeFlag)
                {
                    if(operator.equals("-"))
                    {
                        equation.append(operator);
                        numFlag=false;
                        dotFlag=true;
                        isExpressionCorrect=false;
                        operatorFlag=true;
                        negativeFlag=false;
                    }
                }
            }
        };
        for(int i=0;i<operators.length;i++)
        {
            findViewById(operators[i]).setOnClickListener(listener);
        }
    }

    public void dotClick(View view){
        if(numFlag && dotFlag)
        {
            equation.append(".");
            numFlag=false;
            dotFlag=false;
            operatorFlag=false;
            isExpressionCorrect=false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        equation=findViewById(R.id.tv_equation);
        result=findViewById(R.id.tv_result);
        assigningNumbersListener();
        assigningOperatorsListener();
    }
}
