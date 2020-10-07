package com.example.makaotalk.popup;

import android.graphics.Bitmap;

import java.util.List;

public interface Classifier {

    class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of the object.
         * 인식된 항목에 대한 고유 식별자. 객체의 인스턴스가 아니라 클래스에 고유하다
         */
        private final String id;
        /**
         * Display name for the recognition.
         * 인식체의 이름
         */
        private final String title;
        /**
         * Whether or not the model features quantized or float weights.
         * 모델에 양자화 또는 부동가중치가 있는지 여부
         */
        private final boolean quant;
        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         * 인식이 다른 것에 비해 얼마나 좋은지에 대한 정렬 가능한 점수. 높을수록 좋다
         */
        private final Float confidence;

        public Recognition(final String id, final String title, final Float confidence, final boolean quant) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.quant = quant;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }
            if (title != null) {
                resultString += title + " ";
            }
            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }
            return resultString.trim();
        }
    }

    List<Recognition> recognizeImage(Bitmap bitmap);

    void close();
}
