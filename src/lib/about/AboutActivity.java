package lib.about;


import mobiric.demo.wifiwidget.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity
{

	TextView tvLicences;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// clickable github link
		DefensiveURLSpan.fixTextView((TextView) findViewById(R.id.tvAboutApp));

		// version name
		TextView tvVersion = (TextView) findViewById(R.id.tvAboutVersion);
		try
		{
			String versionName =
					getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			tvVersion.setText(String.format(getString(R.string.about_version_format), versionName));
		}
		catch (NameNotFoundException e)
		{
			tvVersion.setVisibility(View.GONE);
		}
	}

	public void onClickLogoGlowworm(View view)
	{
		gotoWeb(R.string.about_glowworm_url);
	}

	private void gotoWeb(int urlResId)
	{
		Intent browserIntent =
				new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(urlResId)));
		startActivity(browserIntent);
	}

	/**
	 * @see http://commonsware.com/blog/2013/10/23/linkify-autolink-need-custom-urlspan.html
	 */
	private static class DefensiveURLSpan extends URLSpan
	{
		public DefensiveURLSpan(String url)
		{
			super(url);
		}

		@Override
		public void onClick(View widget)
		{
			try
			{
				super.onClick(widget);
			}
			catch (ActivityNotFoundException e)
			{
				// do nothing
			}
		}

		private static void fixTextView(TextView tv)
		{
			SpannableString current = (SpannableString) tv.getText();
			URLSpan[] spans = current.getSpans(0, current.length(), URLSpan.class);

			for (URLSpan span : spans)
			{
				int start = current.getSpanStart(span);
				int end = current.getSpanEnd(span);

				current.removeSpan(span);
				current.setSpan(new DefensiveURLSpan(span.getURL()), start, end, 0);
			}
		}
	}

}
