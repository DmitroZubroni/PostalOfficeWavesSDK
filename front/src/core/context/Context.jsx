import {createContext} from "react";
const AtlantContext = createContext({})

const AppProvider = ({ children }) => {


    const values = {

    }

    return <AppProvider value={values}>{children}</AppProvider>
}

export { AppProvider, AtlantContext }