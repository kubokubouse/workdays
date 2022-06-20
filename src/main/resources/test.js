/*
 * This code is for Internal Salesforce use only, and subject to change without notice.
 * Customers shouldn't reference this file in any web pages.
 */
var DomainSwitcher = function () {
    function g(a) {
      if (a = document.getElementById(a)) a.style.display = "block"
    }
  
    function f(a) {
      if (a = document.getElementById(a)) a.style.display = "none"
    }
  
    function k(a) {
      var c = document.getElementById("mydomain_preview"),
        d = document.getElementById("mydomain_suffix").value,
        e = document.getElementById("community_suffix").value,
        b = a.value.replace(/^\s+|\s+$/g, ""),
        h = !1;
      b || (h = !0, b = "<em>domain</em>");
      for (b.match(/^[-./A-Za-z0-9]+$/) && a.removeAttribute("style"); c.firstChild;) c.removeChild(c.firstChild);
      a = b.indexOf(".");
      d = -1 === a ? "https://" + b + d : 0 !== a && 0 === d.indexOf(b.substring(a)) ? "https://" + b.substring(0, a) + d : 0 !== a && 0 === e.indexOf(b.substring(a)) ? "https://" + b.substring(0, a) + e : "https://" + b;
      h ? c.innerHTML = d : (h = document.createTextNode(d), c.appendChild(h))
    }
    return {
      handleMyDomain: function () {
        var a = document.getElementById("mydomain"),
          c = a.value.replace(/^\s+|\s+$/g, "");
        if (c.match(/^[-./A-Za-z0-9]+$/)) {
          var a = window.SFDCSessionVars && window.SFDCSessionVars.usec ? "http://" : "https://",
            d = document.getElementById("mydomain_suffix"),
            e = document.getElementById("community_suffix"),
            b = c.indexOf("."),
            a = -1 === b ? a + (c + d.value) : 0 !== b && 0 === d.value.indexOf(c.substring(b)) ? a + (c.substring(0, b) + d.value) : 0 !== b && 0 === e.value.indexOf(c.substring(b)) ? a + (c.substring(0, b) + e.value) : a + c;
          (c = document.getElementById("pqs")) ? c.value && "?" === c.value.substring(0, 1) && (a += c.value): (c = document.getElementById("login_startUrl")) && (c.value && "" !== c.value) && (a = encodeURIComponent ? a + ("?startURL=" + encodeURIComponent(c.value)) : a + ("?startURL=" + c.value));
          window.location = a
        } else a.style.border = "#f00 1px solid"
      },
      updateMyDomain: function (a, c) {
        k(a);
        var d = document.getElementById("mydomain_suffix").value,
          e = document.getElementById("community_suffix").value,
          b = d.indexOf(":"),
          d = -1 === b ? d : d.substring(0, b),
          b = e.indexOf(":"),
          e = -1 === b ? e : e.substring(0, b),
          d = [d, e],
          e = a.value;
        if (a && (0 !== a.value.length && c) && (b = a.setSelectionRange ? c.which : c.keyCode, !(32 > b || 33 <= b && 46 >= b || 112 <= b && 123 >= b))) {
          var h;
          a.setSelectionRange ? h = a.selectionStart : (b = document.selection.createRange(), b.parentElement() === a && (h = a.value.lastIndexOf(b.text)));
          for (var b = e.indexOf("."), f = 0; f < d.length; f++) {
            var g = d[f];
            if (0 < b && 0 === g.toLowerCase().indexOf(e.toLowerCase().substring(b))) {
              a.value += g.substring(e.length - b, g.length);
              break
            }
          }
          a.setSelectionRange ? a.setSelectionRange(h, a.value.length) : (b = a.createTextRange(), b.moveStart("character", h), b.moveEnd("character", a.value.length), b.select())
        }
      },
      enterCustomDomain: function (a, c) {
        g("mydomainContainer");
        g("header");
        document.title = a + c;
        var d = document.getElementById("header");
        d && (d.innerHTML = a);
        f("theloginform");
        f("signup");
        d = document.getElementById("mydomain");
        k(d);
        0 < d.offsetWidth && (f("mydomain_preview"), document.getElementById("mydomain_preview").style.maxWidth = d.offsetWidth + "px", g("mydomain_preview"));
        d.focus()
      },
      dismissCustomDomain: function (a, c) {
        f("mydomainContainer");
        f("header");
        g("theloginform");
        g("signup");
        f("error");
        document.title = a + c;
        document.getElementById("assistive-announce").innerHTML = a
      }
    }
  }();
  var IdpOptions = function () {
    function e(a) {
      if (a = document.getElementById(a)) a.style.display = "block"
    }
  
    function d(a) {
      if (a = document.getElementById(a)) a.style.display = "none"
    }
  
    function p(a) {
      var b = document.getElementById("header");
      b && (b.innerHTML = a)
    }
  
    function q() {
      if (!(0 < k.length)) {
        var a = document.getElementById("idplist");
        if (a)
          for (var a = a.childNodes, b = 0; b < a.length; b++) k.push({
            name: a[b].getElementsByTagName("span")[0].innerHTML,
            elem: a[b]
          })
      }
    }
  
    function r(a, b, f) {
      for (var c = a.elem.firstChild; c.lastChild && "span" === c.lastChild.nodeName.toLowerCase();) c.removeChild(c.lastChild);
      if (null !== b && null !== f) {
        var g = "",
          d = null,
          e = null,
          h = null;
        f = f.length;
        0 === b ? (d = a.name.substring(0, f), e = a.name.substring(f), g = "a") : (g = "b", d = a.name.substring(0, b), b + f >= a.name.length ? e = a.name.substring(b) : (e = a.name.substring(b, b + f), h = a.name.substring(b + f)));
        b = document.createElement("span");
        b.appendChild(document.createTextNode(d));
        "a" === g && (b.style.fontWeight = "bolder");
        c.appendChild(b);
        null !== e && (d = document.createElement("span"), d.appendChild(document.createTextNode(e)), "b" === g && (d.style.fontWeight = "bolder"), c.appendChild(d));
        null !== h && (g = document.createElement("span"), g.appendChild(document.createTextNode(h)), c.appendChild(g))
      } else h = document.createElement("span"), h.appendChild(document.createTextNode(a.name)), c.appendChild(h);
      return a.elem
    }
  
    function m(a) {
      s();
      var b = document.getElementById("idplist").childNodes;
      "down" === a && h < b.length - 1 ? h++ : "up" === a && -1 < h && h--;
      if (-1 < h && h < b.length && (a = b[h].firstChild, document.getElementById("idp_search").setAttribute("aria-activedescendant", a.id), a.setAttribute("style", "background-color: #eef1f6;"), 1 < b.length && (document.getElementById("idp_search").value = a.getAttribute("title")), 4 < b.length)) {
        a = document.getElementById("idpscrollable");
        var f = a.scrollTop,
          c = b[h].offsetHeight * h;
        c < f ? a.scrollTop = c : c > f + a.clientHeight && (a.scrollTop = c - 3 * b[h].offsetHeight)
      }
    }
  
    function s() {
      for (var a = document.getElementById("idplist").childNodes, b = 0; b < a.length; b++) a[b].firstChild.setAttribute("style", "background-color:white;")
    }
  
    function n() {
      return "idphint_" + window.location.pathname
    }
  
    function t(a) {
      if ("button" === a.nodeName.toLowerCase()) return a.cloneNode(!0);
      var b = a.firstChild;
      a = document.createElement("button");
      a.setAttribute("class", "button mb8 secondary wide");
      a.setAttribute("onclick", b.getAttribute("onclick"));
      var f = b.firstChild,
        b = b.lastChild; - 1 === f.src.indexOf("/img/s.gif") && a.appendChild(f.cloneNode(!0));
      a.appendChild(b.cloneNode(!0));
      return a
    }
    var k = [],
      h = -1,
      l = null;
    try {
      l = window.localStorage ? window.localStorage : null
    } catch (u) {}
    return {
      enterIdp: function (a, b) {
        e("idp_section_chooser");
        e("cancel_idp_div");
        e("header");
        document.title = a + b;
        p(a);
        d("content");
        d("choose_idp");
        d("employee_login");
        document.getElementById("idp_search").focus()
      },
      dismissIdp: function (a, b) {
        d("idp_section_chooser");
        d("cancel_idp_div");
        d("header");
        d("error");
        e("content");
        e("choose_idp");
        e("employee_login");
        document.title = a + b
      },
      searchIdps: function (a) {
        q();
        if (38 === a.keyCode) m("up");
        else if (40 === a.keyCode) m("down");
        else if (13 === a.keyCode) a = document.getElementById("idplist").childNodes, -1 < h && h < a.length && a[h].firstChild.click();
        else {
          h = -1;
          s();
          document.getElementById("idp_search").setAttribute("aria-activedescendant", "");
          document.getElementById("idpscrollable").scrollTop = 0;
          a = document.getElementById("idp_search").value;
          for (var b = [], f = [], c = document.getElementById("idplist"); c.firstChild;) c.removeChild(c.firstChild);
          var c = document.getElementById("idplist"),
            g;
          for (g = 0; g < k.length; g++) {
            var d = k[g],
              e = RegExp(a.toLowerCase().replace(/[\-\/\\\^$*+?.()|\[\]{}]/g, "\\$&"));
            d.name.toLowerCase().match(e) && (e = d.name.toLowerCase().search(e), 0 === e ? b.push(r(d, e, a)) : f.push(r(d, e, a)))
          }
          for (g = 0; g < b.length; g++) c.appendChild(b[g]);
          for (g = 0; g < f.length; g++) c.appendChild(f[g]);
          1 === c.childNodes.length && m("down")
        }
      },
      useIdp: function (a, b) {
        if (null !== l) 
        try {
          l.setItem(n(), a)
        } catch (d) {}
        window.location.href = b
      },
      checkIdpHint: function (a, b) {
        if (null === l) return !1;
        var f = l.getItem(n());
        if (f) {
          q();
          if (!(0 < k.length)) {
            var c = document.getElementById("idp_section_buttons");
            if (c)
              for (var c = c.childNodes, g = 0; g < c.length; g++) k.push({
                name: c[g].getElementsByTagName("span")[0].innerHTML,
                elem: c[g]
              })
          }
          for (c = 0; c < k.length; c++)
            if (k[c].name === f) return document.getElementById("idp_hint").appendChild(t(k[c].elem)), e("idp_hint_section"), e("header"), document.title = a + b, p(a), d("content"), d("choose_idp"), d("or_use_idp"), d("idp_section_buttons"), d("idp_section_chooser"), !0
        } else if ((f = document.getElementById("idp_section_chooser")) && "none" !== f.style.display)(f = document.getElementById("idp_search")) && f.focus();
        return !1
      },
      clearIdpHint: function (a) {
        null !== l && l.removeItem(n());
        d("idp_hint_section");
        a && (document.getElementById("login_form") ? (d("header"), d("error"), e("content"), e("choose_idp"), e("or_use_idp"), e("idp_section_buttons")) : (e("idp_section_buttons"), e("idp_section_chooser"), (a = document.getElementById("idp_search")) && a.focus()))
      }
    }
  }();
  var LoginHint = function (b) {
    function q(a, f) {
      (!b.lh || !document.getElementById("username")) && IdpOptions.checkIdpHint(b.hidp, b.title) ? k && (window.clearTimeout(k), k = null) : SfdcApp.SfdcSession.getIdentities({
        get: [],
        callback: function (c) {
          k && (window.clearTimeout(k), k = null);
          try {
            var d = c.identities,
              e = document.getElementById("login_dch");
            if (e && e.value) {
              var h = n(d);
              if (1 >= h.num) {
                c = null;
                for (var w in h.res) c = h.res[w].identity.instance;
                if (0 == h.num || c == e.value) {
                  var l = document.getElementById("login_startUrl");
                  window.location = e.value + l.value;
                  return
                }
              }
            }
            if (a) {
              var g = n(d).res,
                x = p(g);
              LoginHint.Ui.renderEdit(x, b.ah, b.ih, b.save, b.lllbl)
            } else r(d);
            f()
          } catch (y) {
            s()
          }
        }
      })
    }
  
    function p(a) {
      var b = [],
        c = [],
        d;
      for (d in a)
        if (a.hasOwnProperty(d)) {
          var e = a[d];
          e.active ? b.push(e) : c.push(e)
        }
      a = b.sort(function (a, c) {
        return c.lastused - a.lastused
      });
      c = c.sort(function (a, c) {
        return c.lastused - a.lastused
      });
      return a.concat(c)
    }
  
    function n(a) {
      var f = 0,
        c = [],
        d = b.im,
        e = b.ic,
        h;
      for (h in a)
        if (a.hasOwnProperty(h)) {
          var g = a[h];
          if (e || !g.community)
            if (!(d && !g.mydomain || e && !g.community))
              if (!((d || e) && b.ur != a[h].identity.instance) && t(a[h].identity.instance)) f++, c[h] = g
        }
      return {
        res: c,
        num: f
      }
    }
  
    function r(a) {
      var f = n(a);
      a = f.res;
      var f = f.num,
        c = b.im,
        d = b.ic,
        e = b.iaac;
      a = 1 < f ? p(a) : a;
      LoginHint.Ui.renderIdentities(b.suo, b.sum, b.hac, b.heu, b.lpt, b.ah, b.ih, f, a, b.lh, b.le, b.lp, b.title, c, d, e, b.lllbl);
      SFDCSessionVars.hintRendered = !0
    }
  
    function t(a) {
      return a && 6 < a.length ? (a = a.substring(0, 6), "http:/" === a || "https:" === a) : !1
    }
  
    function s() {
      var a = document.getElementById("content");
      a && (a.style.display = "block", loader());
      if (a = document.getElementById("signup")) a.style.display = "block";
      if (a = document.getElementById("choose_idp")) a.style.display = "block";
      k = null
    }
  
    function u(a, b) {
      LoginHint.Ui.useIdentity(a, b, !1)
    }
  
    function v(a) {
      q(a, function () {})
    }
    var k = null,
      g = [];
    return {
      showChooser: function () {
        LoginHint.Ui.showChooser()
      },
      useNewIdentity: function () {
        LoginHint.Ui.useNewIdentity()
      },
      clearExistingIdentity: function () {
        LoginHint.Ui.clearExistingIdentity()
      },
      showEdit: function () {
        g = [];
        v(!0);
        LoginHint.Ui.showEdit()
      },
      hideLoginForm: function () {
        if (window.setTimeout && !SfdcApp.SfdcSession.disabled) {
          var a = !1;
          try {
            var b = document.styleSheets,
              c;
            for (c in b)
              if (b.hasOwnProperty && b.hasOwnProperty(c)) {
                var d = b[c];
                if (d.cssRules) {
                  d.insertRule ? (d.insertRule("#content { display:none; }", d.cssRules.length), d.insertRule("#signup { display:none; }", d.cssRules.length), d.insertRule("#choose_idp { display:none; }", d.cssRules.length), a = !0) : d.addRule && (d.addRule("#content", "display:none;"), d.addRule("#signup", "display:none;"), d.addRule("#choose_idp", "display:none;"), a = !0);
                  break
                }
              }
          } catch (e) {}
          a && (k = window.setTimeout(s, 1500))
        }
      },
      useIdentity: u,
      getSavedIdentities: v,
      sortResults: p,
      deleteIdentity: function (a, f) {
        g.push({
          oid: a,
          uid: f
        });
        var c = document.getElementById("edit-" + (a + f));
        c.parentNode.removeChild(c);
        document.getElementById("hint_save_edit").innerHTML = 1 === g.length ? b.saveOne : b.saveMany.replace("#p#", g.length);
        document.getElementById("assistive-announce").innerHTML = b.dc
      },
      saveHintEdit: function () {
        if (0 < g.length) {
          for (var a, f = 0; f < g.length; f++) {
            var c = g[f],
              d = document.getElementById("hint_" + (c.oid + c.uid));
            a = d.parentNode;
            a.removeChild(d);
            SfdcApp.SfdcSession.deleteIdentity({
              oid: c.oid,
              uid: c.uid
            })
          }
          0 === a.childNodes.length ? LoginHint.Ui.noSavedIdentity() : LoginHint.showChooser();
          LoginHint.Ui.updateSavedText(b.suo, b.sum, a.childNodes.length)
        } else LoginHint.showChooser()
      },
      renderIdentities: r,
      executeActive: function (a, b, c, d, e, g, k, l) {
        if (t(d))
          if (new Date(c) < new Date) {
            var m = null;
            l = function () {
              m && (window.clearTimeout(m), m = null);
              q(!1, function () {
                if (k) {
                  var a = d,
                    b = document.getElementById("pqs"),
                    a = b && b.value ? a + (b.value + "&") : a + "?";
                  window.location = a + "stbdtimeout=1&login_hint=" + encodeURIComponent(e)
                } else u(e, g)
              })
            };
            window.setTimeout && (m = window.setTimeout(l, 1E3));
            SfdcApp.SfdcSession.inactive({
              oid: a,
              uid: b,
              callback: l
            })
          } else c = "", l && (c += "&icom=1"), (l = document.getElementById("pqs")) && (l.value && "?" === l.value.substring(0, 1)) && (c += "&q=" + encodeURIComponent(l.value)), window.location = d + "/secur/loginhint?o=" + a + c + "&u=" + b + "&un=" + encodeURIComponent(e) + "&retURL=" + encodeURIComponent(document.getElementById("login_startUrl").value)
      }
    }
  }(SFDCSessionVars);
  if (!this.LoginHint) var LoginHint = {};
  LoginHint.Ui = function () {
    function g(a) {
      if (a = document.getElementById(a)) a.style.display = "block"
    }
  
    function h(a) {
      if (a = document.getElementById(a)) a.style.display = "none"
    }
  
    function I(a) {
      var b = document.getElementById("header");
      b && (b.innerHTML = a)
    }
  
    function J(a) {
      var b = document.getElementById("assistive-announce");
      b && (b.innerHTML = a)
    }
  
    function p() {
      g("content");
      u ? (g("chooser"), g("use_new_identity_div"), g("header"), I(x), document.title = x + y, J(x)) : (h("chooser"), h("use_new_identity_div"));
      z ? (g("manager"), g("header"), I(B), document.title = B + y) : h("manager");
      q ? (g("error"), g("chooser_error")) : (h("error"), h("chooser_error"));
      null !== l && (document.getElementById("username").value = l);
      r ? (g("theloginform"), g("signup"), g("employee_login"), g("or_use_idp"), g("idp_section_buttons"), g("choose_idp"), h("header"), document.title = C + y, J(C), w ? (g("username"), h("idcard-container")) : (h("username"), g("idcard-container"))) : (h("theloginform"), h("signup"), h("employee_login"), h("or_use_idp"), h("idp_section_buttons"), h("choose_idp"));
      var a = document.getElementById(v);
      r && (a && a.focus) && a.focus()
    }
  
    function A(a) {
      u = !0;
      z = r = !1;
      q = a ? !0 : !1;
      p()
    }
  
    function G(a, b) {
      u = !1;
      q = w = r = !0;
      z = !1;
      l = a ? a : D ? "" : null;
      v = b ? "password" : "username";
      p()
    }
  
    function H(a, b, c) {
      u = !1;
      r = !0;
      q = w = !1;
      l = a;
      v = "password";
      if ((a = document.getElementById("rememberUn")) && !c) a.checked = !0;
      document.getElementById("idcard").setAttribute("aria-label", l);
      c = document.getElementById("idcard-avatar");
      c.src = b;
      c.title = l;
      b = document.getElementById("idcard-identity");
      b.innerHTML = "";
      b.appendChild(document.createTextNode(l));
      p()
    }
  
    function K() {
      u = !1;
      w = r = !0;
      q = !1;
      l = "";
      v = "username";
      p()
    }
  
    function L() {
      z = u = !1;
      q = w = r = !0;
      l = null;
      v = "username";
      p()
    }
  
    function E(a, b, c) {
      if (a.addEventListener) a.addEventListener(b, c, !1);
      else if (a.attachEvent) a.attachEvent("on" + b, c);
      else throw "Could not add handler " + b;
    }
  
    function Q(a) {
      a = a || window.event;
      a = m[(a.target || a.srcElement).paramkey];
      LoginHint.deleteIdentity(a.oid, a.uid)
    }
  
    function R(a, b, c, d) {
      var k;
      k = a.identity.display ? a.identity.display : a.identity.username;
      var e = a.identity.oid + a.identity.uid,
        f = document.createElement("li");
      f.setAttribute("id", "edit-" + e);
      d = M(a, k, a.identity.ll, d);
      b = N(a, b, c);
      k = O(a, k);
      e = "editp" + e + (new Date).getTime();
      m[e] = {
        oid: a.identity.oid,
        uid: a.identity.uid,
        username: a.identity.username
      };
      a = document.createElement("a");
      a.setAttribute("href", "#");
      a.setAttribute("onclick", "return false;");
      a.setAttribute("class", "hintclearlink");
      a.paramkey = e;
      E(a, "click", Q);
      c = document.createElement("img");
      c.setAttribute("alt", "");
      c.setAttribute("class", "hintclearicon");
      c.setAttribute("src", "/img/clear.png");
      c.paramkey = e;
      a.appendChild(c);
      f.appendChild(d);
      f.appendChild(b);
      f.appendChild(a);
      f.appendChild(k);
      return f
    }
  
    function S(a) {
      a = a || window.event;
      a = m[(a.target || a.srcElement).paramkey];
      LoginHint.executeActive(a.oid, a.uid, a.expire, a.instance, a.username, a.thumbnail, a.otherDom, a.community)
    }
  
    function T(a) {
      a = a || window.event;
      window.location = m[(a.target || a.srcElement).paramkey].redir
    }
  
    function U(a) {
      a = a || window.event;
      a = m[(a.target || a.srcElement).paramkey];
      if (a.ll) {
        var b = document.getElementById("username");
        if (b) {
          b.value = a.username;
          if (a = document.getElementById("password")) a.value = "";
          handleLogin && handleLogin();
          document.getElementById("login_form").submit()
        } else LoginHint.useIdentity(a.username, a.thumbnail)
      } else LoginHint.useIdentity(a.username, a.thumbnail)
    }
  
    function P(a, b, c, d, k) {
      var e = !b && (a.mydomain || a.community),
        f = a.identity.oid + a.identity.uid,
        g;
      g = a.identity.display ? a.identity.display : a.identity.username;
      b = document.createElement("li");
      b.setAttribute("id", "hint_" + f);
      var s = document.createElement("a");
      s.setAttribute("href", "#");
      s.setAttribute("onclick", "return false;");
      f = "hintclick" + f + (new Date).getTime();
      if (a.active) m[f] = {
        oid: a.identity.oid,
        uid: a.identity.uid,
        expire: a.expire,
        instance: a.identity.instance,
        username: a.identity.username,
        thumbnail: a.identity.thumbnail,
        otherDom: e,
        community: a.community
      }, E(s, "click", S);
      else if (e) {
        var e = a.identity.instance,
          h = document.getElementById("pqs"),
          e = h && h.value ? e + (h.value + "&") : e + "?",
          e = e + "stbdtimeout=1&",
          e = a.identity.ll ? e + "llpost=1&un=" : e + "login_hint=",
          e = e + encodeURIComponent(a.identity.username);
        m[f] = {
          redir: e
        };
        E(s, "click", T)
      } else m[f] = {
        username: a.identity.username,
        thumbnail: a.identity.thumbnail,
        ll: a.identity.ll
      }, E(s, "click", U);
      s.paramkey = f;
      b.paramkey = f;
      k = M(a, g, a.identity.ll, k);
      k.paramkey = f;
      s.appendChild(k);
      c = N(a, c, d);
      s.appendChild(c);
      a = O(a, g);
      a.paramkey = f;
      s.appendChild(a);
      b.appendChild(s);
      return b
    }
  
    function M(a, b, c, d) {
      var k = document.createElement("img");
      k.setAttribute("alt", "");
      k.setAttribute("title", b);
      k.setAttribute("src", a.identity.thumbnail);
      if (c) return k.setAttribute("class", "thumbnail_img"), a = document.createElement("div"),
        a.setAttribute("class", "thumbnail_div"), b = document.createElement("img"), b.setAttribute("class", "thumbll_icon"), b.setAttribute("alt", d), b.setAttribute("title", d), b.setAttribute("src", "/img/lightninglogin.svg"), a.appendChild(k), a.appendChild(b), a;
      k.setAttribute("class", "thumbnail");
      return k
    }
  
    function N(a, b, c) {
      var d = document.createElement("img");
      d.setAttribute("class", "activity");
      d.setAttribute("src", a.active ? "/img/greendot.png" : "/img/greydot.png");
      d.setAttribute("alt", a.active ? b : c);
      return d
    }
  
    function O(a, b) {
      var c = document.createElement("span");
      c.appendChild(document.createTextNode(b));
      return c
    }
  
    function F(a, b, c) {
      var d = document.getElementById("hint_back_chooser");
      d || (d = document.createElement("a"), d.setAttribute("id", "hint_back_chooser"), d.setAttribute("href", "#"), d.setAttribute("onclick", "LoginHint.showChooser(); return false;"), d.setAttribute("class", "fr small"), document.getElementById("usernamegroup").insertBefore(d, document.getElementById("username_container")));
      1 == c ? d.innerHTML = a : 1 < c ? d.innerHTML = b.replace("#p#", c) : d.parentNode.removeChild(d);
      if (d = document.getElementById("chooser_label")) d.innerHTML = 1 == c ? a : b.replace("#p#", c)
    }
    var D = !1,
      u = !1,
      r = !1,
      w = !0,
      q = !1,
      z = !1,
      l = "",
      v = "password",
      m = {},
      x = "",
      B = "",
      C = "",
      y = "";
    return {
      showChooser: A,
      useNewIdentity: K,
      showEdit: function () {
        u = !1;
        z = !0;
        p()
      },
      updateSavedText: F,
      clearExistingIdentity: function () {
        w = !0;
        l = "";
        v = "username";
        q = !1;
        p()
      },
      useIdentity: H,
      renderEdit: function (a, b, c, d, k) {
        document.getElementById("hint_save_edit").innerHTML = d;
        d = a.length;
        for (var e = document.getElementById("editlist"); e.firstChild;) e.removeChild(e.firstChild);
        var f;
        for (f = 0; f < d; f++) e.appendChild(R(a[f], b, c, k))
      },
      renderIdentities: function (a, b, c, d, k, e, f, g, h, l, m, p, u, q, r, w, v) {
        "" === x && (x = c);
        "" === B && (B = d);
        "" === C && (C = k);
        "" === y && (y = u);
        for (c = document.getElementById("idlist"); c.firstChild;) c.removeChild(c.firstChild);
        if (0 === g) D = !1, L(), F(a, b, 0);
        else if (1 === g) {
          D = !0;
          var t = null,
            n;
          for (n in h) h.hasOwnProperty(n) && (t = h[n]);
          F(a, b, 1);
          c.appendChild(P(t, q || r, e, f, v));
          m ? G(t.identity.username) : l || (t.active ? A() : t.identity.ll ? A() : H(t.identity.username, t.identity.thumbnail, !1))
        } else {
          D = !0;
          d = h.length;
          for (n = 0; n < d; n++) c.appendChild(P(h[n], q || r, e, f, v));
          F(a, b, g);
          if (m) {
            a = window.location.search.substring(1).split("&");
            for (n = 0; n < a.length; n++)
              if (b = a[n].split("="), "ec" === b[0]) {
                t = b[1];
                break
              }
            "301" === t || "302" === t ? (document.getElementById("chooser_error").innerHTML = document.getElementById("error").innerHTML, A(!0)) : G()
          } else l || A()
        }
        l && (m ? G(p, !0) : H(p, "/img/userprofile/default_profile_45.png", !0));
        w && K()
      },
      noSavedIdentity: L
    }
  }();
  //# sourceMappingURL=/javascript/1635874031000/sfdc/source/LoginHint208.js.map