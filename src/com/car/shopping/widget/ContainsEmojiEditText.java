package com.car.shopping.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

public class ContainsEmojiEditText extends EditText{
	//�������ǰ�Ĺ��λ��
    private int cursorPos;
    //�������ǰEditText�е��ı�
    private String inputAfterText;
    //�Ƿ�������EditText������
    private boolean resetText;

    private Context mContext;

    public ContainsEmojiEditText(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }
    public ContainsEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEditText();
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEditText();
    }
    
 // ��ʼ��edittext �ؼ�
    private void initEditText() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    cursorPos = getSelectionEnd();
                    // ������s.toString()����ֱ����s����Ϊ�����s��
                    // ��ô��inputAfterText��s���ڴ���ָ�����ͬһ����ַ��s�ı��ˣ�
                    // inputAfterTextҲ�͸ı��ˣ���ô������˾�ʧ����
                    inputAfterText= s.toString();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    if (count >= 2) {//������ŵ��ַ�������СΪ2
                        CharSequence input = s.subSequence(cursorPos, cursorPos + count);
                        if (containsEmoji(input.toString())) {
                            resetText = true;
                            Toast.makeText(mContext, "��֧������Emoji�������", Toast.LENGTH_SHORT).show();
                            //�Ǳ�����žͽ��ı���ԭΪ����������֮ǰ������
                            setText(inputAfterText);
                            CharSequence text = getText();
                            if (text instanceof Spannable) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, text.length());
                            }
                        }
                    }
                } else {
                    resetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    
    /**
     * ����Ƿ���emoji����
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //�������ƥ��,����ַ���Emoji����
                return true;
            }
        }
        return false;
    }
    
    /**
     * �ж��Ƿ���Emoji
     *
     * @param codePoint �Ƚϵĵ����ַ�
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    public boolean isEmoji(String string) {
        Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }
    
}