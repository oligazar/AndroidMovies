package us.kostenko.architecturecomponentstmdb.details.repository

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
        // Data + showLoading() -> InProgress
        assert(State.Success("Data").showLoading() == State.Loading)
        // InProgress + showLoading() -> InProgress
        assert(State.Loading.showLoading() == State.Loading)
        // Error + showLoading() -> InProgress
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
        // InProgress + showData() -> InProgress
        assert(State.Loading.showData("Data") == State.Success("Data"))
        // Error + showData() -> InProgress
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
        // InProgress + showError() -> InProgress
        assert(State.Loading.showError("Error") == State.Error(Event("Error")))
        // Error + showError() -> Error
        assert(State.Error(Event("Error")).showError("NewError") == State.Error(Event("NewError")))
    }
}