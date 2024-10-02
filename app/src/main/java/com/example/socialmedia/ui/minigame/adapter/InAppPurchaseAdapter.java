//package com.example.socialmedia.ui.minigame.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import com.example.socialmedia.ui.minigame.di.Constant;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class InAppPurchaseAdapter extends RecyclerView.Adapter<InAppPurchaseAdapter.ViewHolder> {
//
//    private OnClickListener onClickListener;
//    private List<ProductDetails> productDetailsList = new ArrayList<>();
//    private Context context;
//
//    public void setData(Context context,
//                        List<ProductDetails> productDetailsList) {
//        this.productDetailsList = productDetailsList;
//       productDetailsList this.context = context;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View rootView = LayoutInflater.from(context).inflate(R.layout.item_subcription, parent, false);
//        return new ViewHolder(rootView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.display(productDetailsList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return productDetailsList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView tvSubName;
//        private AppCompatButton item;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvSubName = itemView.findViewById(R.id.tvSubname);
//            item = itemView.findViewById(R.id.price);
//        }
//
//        public void display(ProductDetails productDetails) {
//            if (productDetails == null) return;
//            String price = productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
//            tvSubName.setText(price);
//            item.setText(setTitleValue(productDetails.getProductId()));
//            item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onClickListener.onClickItem(productDetails);
//                }
//            });
//        }
//    }
//
//    public void setOnClickListener(OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }
//
//    public interface OnClickListener {
//        void onClickItem(ProductDetails item);
//    }
//
//    private String setTitleValue(String productId) {
//        switch (productId) {
//            case Constant.KEY_QUIZ_1:
//                return "10 coin";
//            case Constant.KEY_QUIZ_2:
//                return "50 coin";
//            case Constant.KEY_QUIZ_3:
//                return "100 coin";
//            case Constant.KEY_QUIZ_4:
//                return "300 coin";
//            case Constant.KEY_QUIZ_5:
//                return "600 coin";
//            case Constant.KEY_QUIZ_6:
//                return "900 coin";
//            case Constant.KEY_QUIZ_7:
//                return "1200 coin";
//            case Constant.KEY_QUIZ_8:
//                return "1500 coin";
//            default:
//                return "0 coin";
//        }
//    }
//
//}
//
