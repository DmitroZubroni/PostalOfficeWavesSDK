import {Link} from "react-router-dom";

const Header = () => {
    return (
        <div style={{ backgroundColor: "#5023ed"}}>
            <h2> Postal Office</h2>
            <Link to="/" className="btn" style={{color:"white"}}> личный кабинет </Link>
            <Link to="/admin" className="btn" style={{color:"white"}}> админгистратор</Link>
            <Link to="/employee" className="btn" style={{color:"white"}}> сотрудник</Link>
            <Link to="/userFunc" className="btn" style={{color:"white"}}> отправления </Link>
        </div>
    )
}
export default Header;