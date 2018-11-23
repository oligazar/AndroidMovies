package us.kostenko.architecturecomponentstmdb.tools

import android.text.TextUtils
import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.core.internal.deps.guava.base.Preconditions.checkArgument
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.TypeSafeMatcher
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.BDDMockito


fun <T> mockPagedList(list: List<T>): PagedList<T> = mock {
    on { get(anyInt()) } doAnswer { invocation ->
        val index = invocation.arguments.first() as Int
        list[index]
    }
    on { size } doReturn list.size
}

infix fun <T> T?.willReturn(value: T): BDDMockito.BDDMyOngoingStubbing<T?> =
        given(this).willReturn(value)

infix fun <T> T?.willThrow(throwable: Throwable): BDDMockito.BDDMyOngoingStubbing<T?> =
        given(this).willThrow(throwable)

class RecyclerViewItemCountAssertion private constructor(private val matcher: Matcher<Int>) : ViewAssertion {

    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter!!.itemCount, matcher)
    }

    companion object {

        fun withItemCount(expectedCount: Int): RecyclerViewItemCountAssertion {
            return withItemCount(`is`(expectedCount))
        }

        fun withItemCount(matcher: Matcher<Int>): RecyclerViewItemCountAssertion {
            return RecyclerViewItemCountAssertion(matcher)
        }
    }
}

/**
 * A custom [Matcher] which matches an item in a [RecyclerView] by its text.
 *
 *
 *
 * View constraints:
 *
 *  * View must be a child of a [RecyclerView]
 *
 *
 * @param itemText the text to match
 * @return Matcher that matches text in the given view
 */
fun withItemText(itemText: String): Matcher<View> {
    checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty")
    return object : TypeSafeMatcher<View>() {
        public override fun matchesSafely(item: View): Boolean {
            return allOf(
                    isDescendantOfA(isAssignableFrom(RecyclerView::class.java)),
                    withText(itemText)).matches(item)
        }

        override fun describeTo(description: Description) {
            description.appendText("is isDescendantOfA RV with text $itemText")
        }
    }
}