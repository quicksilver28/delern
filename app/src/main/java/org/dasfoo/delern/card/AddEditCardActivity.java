package org.dasfoo.delern.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Level;

/**
 * Activity to edit or add a new card.
 */
public class AddEditCardActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * IntentExtra R.string for the title of this activity.
     */
    public static final String LABEL = "label";

    /**
     * IntentExtra Deck ID which this card belongs to.
     */
    public static final String DECK_ID = "mDeckId";

    /**
     * IntentExtra Card ID being edited.
     */
    public static final String CARD = "card";

    private String mDeckId;
    private TextInputEditText mFrontSideInputText;
    private TextInputEditText mBackSideInputText;
    private Card mCard;
    private CheckBox mAddReversedCardCheckbox;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_card_activity);
        configureToolbar();
        Intent intent = getIntent();
        final int label = intent.getIntExtra(LABEL, 0);
        mDeckId = intent.getStringExtra(DECK_ID);
        mCard = intent.getParcelableExtra(CARD);
        this.setTitle(label);

        mFrontSideInputText = (TextInputEditText) findViewById(R.id.front_side_text);
        mBackSideInputText = (TextInputEditText) findViewById(R.id.back_side_text);
        mAddReversedCardCheckbox = (CheckBox) findViewById(R.id.add_reversed_card_checkbox);
        Button mAddCardToDbButton = (Button) findViewById(R.id.add_card_to_db);
        if (mCard != null) {
            mAddCardToDbButton.setText(R.string.save);
            mFrontSideInputText.setText(mCard.getFront());
            mBackSideInputText.setText(mCard.getBack());
        }
        mAddCardToDbButton.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.add_card_to_db) {
            if (mCard == null) {
                String frontCardSide = mFrontSideInputText.getText().toString();
                String backCardSide = mBackSideInputText.getText().toString();
                addNewCard(frontCardSide, backCardSide);
                if (mAddReversedCardCheckbox.isChecked()) {
                    addNewCard(backCardSide, frontCardSide);
                    Toast.makeText(this, R.string.add_extra_reversed_card_message,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.added_card_user_message,
                            Toast.LENGTH_SHORT).show();
                }
                cleanTextFields();
            } else {
                mCard.setFront(mFrontSideInputText.getText().toString());
                mCard.setBack(mBackSideInputText.getText().toString());
                Card.updateCard(mCard, mDeckId);
                Toast.makeText(this, R.string.updated_card_user_message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void cleanTextFields() {
        mFrontSideInputText.setText("");
        mBackSideInputText.setText("");
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addNewCard(final String frontSide, final String backSide) {
        Card newCard = new Card();
        newCard.setFront(frontSide);
        newCard.setBack(backSide);
        newCard.setLevel(Level.L0.name());
        newCard.setRepeatAt(System.currentTimeMillis());
        Card.createNewCard(newCard, mDeckId);
    }
}
