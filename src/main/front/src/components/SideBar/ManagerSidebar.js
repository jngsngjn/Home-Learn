import React, { useState, useEffect } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import './ManagerSidebar.css';

const ManagerSidebar = () => {
  const [currentDate, setCurrentDate] = useState('');
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const location = useLocation();

  useEffect(() => {
    const date = new Date();
    const formattedDate = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} (${['일', '월', '화', '수', '목', '금', '토'][date.getDay()]})`;
    setCurrentDate(formattedDate);
  }, []);

  useEffect(() => {
    setDropdownOpen(location.pathname.includes('/managers/contact-'));
  }, [location]);

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

  const isActive = (path) => {
    if (path === '/managers') {
      return location.pathname === '/managers' || location.pathname === '/managers/';
    }
    return location.pathname.startsWith(path);
  };

  return (
      <div className="sideBar_container">
        <div className="sideBar_content">
          <div className="sideBar_date_box">
            <span className="sideBar_date">{currentDate}</span>
          </div>
          <div className="sideBar_link">
            <ul className="sideBar_menu">
              <li><NavLink to="/managers" end className={({ isActive }) => isActive ? 'sideBar_link active' : 'sideBar_link'}>대시보드</NavLink></li>
              <li><NavLink to="/managers/manage-curriculums" className={({ isActive }) => isActive ? 'sideBar_link active' : 'sideBar_link'}>교육 과정</NavLink></li>
              <li><NavLink to="/managers/manage-students" className={({ isActive }) => isActive ? 'sideBar_link active' : 'sideBar_link'}>학생 관리</NavLink></li>
              <li><NavLink to="/managers/manage-teachers" className={({ isActive }) => isActive ? 'sideBar_link active' : 'sideBar_link'}>강사 관리</NavLink></li>
              <li><NavLink to="/managers/notice" className={({ isActive }) => isActive ? 'sideBar_link active' : 'sideBar_link'}>공지사항</NavLink></li>
              <li className={`sideBar_dropdown ${dropdownOpen ? 'open' : ''}`}>
                <div className={`sideBar_dropdownHeader ${isActive('/managers/contact-') ? 'active' : ''}`} onClick={toggleDropdown}>
                  문의
                  <span className={`sideBar_dropdownArrow ${dropdownOpen ? 'open' : ''}`}><i class="fa-solid fa-caret-down"></i></span>
                </div>
                <ul className={`sideBar_subMenu ${dropdownOpen ? 'open' : ''}`}>
                  <li><NavLink to="/managers/contact-students" className={({ isActive }) => isActive ? 'sideBar_link active' : 'sideBar_link'}>학생 문의</NavLink></li>
                  <li><NavLink to="/managers/contact-teachers" className={({ isActive }) => isActive ? 'sideBar_link active' : 'sideBar_link'}>강사 문의</NavLink></li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
        <div className="sideBar_line"></div>
      </div>
  );
};

export default ManagerSidebar;