import {AppProvider} from "./core/context/Context.jsx";
import {RouterProvider} from "react-router-dom";
import routing from "./core/routing/Routing.jsx";

const App = () => {

  return (
    <AppProvider>
      <RouterProvider router={routing}/>
    </AppProvider>
  )
}

export default App
