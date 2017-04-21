package com.example.jeff.viewpagerdelete.Miscellaneous;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Jeff on 4/21/17.
 */

public class CollapsibleCardView extends CardView {

  private boolean isCollapsed;

  private RelativeLayout cardHeader;
  private RelativeLayout cardContent;
  private TextView answerTextPreview;

  public CollapsibleCardView(Context context, RelativeLayout cardHeader, RelativeLayout cardContent,
      TextView answerTextPreview) {
    super(context);
    isCollapsed = false;

    this.cardContent = cardContent;
    this.cardHeader = cardHeader;
    this.answerTextPreview = answerTextPreview;

  }

  public CollapsibleCardView(Context context) {
    super(context);
    isCollapsed = false;
  }

  public CollapsibleCardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    isCollapsed = false;
  }

  public CollapsibleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    isCollapsed = false;
  }

  public void collapseContent() {
//    TransitionManager.beginDelayedTransition((ViewGroup)this.getParent());

    cardContent.animate().alpha(0.0f).setDuration(500).withStartAction(new Runnable() {
      @Override
      public void run() {
        answerTextPreview.animate().alpha(1.0f).setDuration(250).start();
      }
    }).start();
    cardContent.setVisibility(View.GONE);
    answerTextPreview.setVisibility(View.VISIBLE);
    isCollapsed = true;

  }

  public void expandContent() {
//    TransitionManager.beginDelayedTransition((ViewGroup)this.getParent());

    cardContent.animate().alpha(1.0f).setDuration(500).withStartAction(new Runnable() {
      @Override
      public void run() {
        answerTextPreview.animate().setDuration(250).alpha(0.0f).start();
      }
    }).start();
    answerTextPreview.setVisibility(View.INVISIBLE);
    cardContent.setVisibility(View.VISIBLE);
    isCollapsed = false;
  }

  public void setCollapsed(boolean collapsed) {
    isCollapsed = collapsed;
    if (isCollapsed) {
      cardContent.setVisibility(View.GONE);
      answerTextPreview.setVisibility(View.VISIBLE);
    } else {
      cardContent.setVisibility(View.VISIBLE);
      answerTextPreview.setVisibility(View.INVISIBLE);
    }
  }

  public boolean isCollapsed() {
    return isCollapsed;
  }

  public RelativeLayout getCardHeader() {
    return cardHeader;
  }

  public void setCardHeader(RelativeLayout cardHeader) {
    this.cardHeader = cardHeader;
    cardHeader.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {

        if (isCollapsed) {
          expandContent();
        } else {
          collapseContent();
        }

      }
    });
  }

  public RelativeLayout getCardContent() {
    return cardContent;
  }

  public void setCardContent(RelativeLayout cardContent) {
    this.cardContent = cardContent;
  }

  public TextView getAnswerTextPreview() {
    return answerTextPreview;
  }

  public void setAnswerTextPreview(TextView answerTextPreview) {
    this.answerTextPreview = answerTextPreview;
  }
}
