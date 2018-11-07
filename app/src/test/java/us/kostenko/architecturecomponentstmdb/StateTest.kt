package us.kostenko.architecturecomponentstmdb

import org.junit.Test
import us.kostenko.architecturecomponentstmdb.common.view.Event
import us.kostenko.architecturecomponentstmdb.details.viewmodel.netres.State

class StateTest {

    @Test
    fun showLoadingTest() {
        // InitialLoading + showLoading() -> InitialLoading
        assert(State.InitialLoading.showLoading() == State.InitialLoading)
        // Retry + showLoading() -> InitialLoading
        assert(State.Retry("Error").showLoading() == State.InitialLoading)
        // Data + showLoading() -> Loading
        assert(State.Success("Data").showLoading() == State.Loading)
        // Loading + showLoading() -> Loading
        assert(State.Loading.showLoading() == State.Loading)
        // Error + showLoading() -> Loading
        assert(State.Error(Event("Error")).showLoading() == State.Loading)
    }

    @Test
    fun showDataTest() {
        // InitialLoading + showData() -> Data
        assert(State.InitialLoading.showData("Data") == State.Success("Data"))
        // Retry + showData() -> Retry
        assert(State.Retry("Error").showData("Data") == State.Retry("Error"))
        // Data + showData() -> Data
        assert(State.Success("Data").showData("NewData") == State.Success("NewData"))
        // Loading + showData() -> Loading
        assert(State.Loading.showData("Data") == State.Success("Data"))
        // Error + showData() -> Loading
        assert(State.Error(Event("Error")).showData("Data") == State.Error(Event("Error")))
    }

    @Test
    fun showErrorTest() {
        // InitialLoading + showError() -> Retry
        assert(State.InitialLoading.showError("Error") == State.Retry("Error"))
        // Retry + showError() -> Retry
        assert(State.Retry("Retry").showError("Error") == State.Retry("Error"))
        // Data + showError() -> Error
        assert(State.Success("Data").showError("Error") == State.Error(Event("Error")))
        // Loading + showError() -> Loading
        assert(State.Loading.showError("Error") == State.Error(Event("Error")))
        // Error + showError() -> Error
        assert(State.Error(Event("Error")).showError("NewError") == State.Error(Event("NewError")))
    }
}